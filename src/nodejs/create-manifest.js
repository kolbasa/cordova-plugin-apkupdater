const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const exec = require('child_process').exec;

const aArguments = process.argv.slice(2);
if (aArguments.length < 4) {
    console.error(
        '\nUSAGE: \n' +
        '\tcreate-manifest.js <version> <chunk-size> <apk-path> <output-path>\n\n' +
        '\tversion:     a string of your choosing, it will not be used by the plugin\n' +
        '\tchunk-size:  the size of one compressed chunk: specified with the units b|k|m, e.g. 500b, 150k, 1m\n' +
        '\tapk-path:    the path to the apk-file\n' +
        '\toutput-path: the path to which the update files are copied\n'
    );
    process.exit(1);
}

const sVersion = aArguments[ 0 ];
const sChunkSize = aArguments[ 1 ];
const sApkPath = path.resolve(aArguments[ 2 ]);
const sUpdatePath = path.resolve(aArguments[ 3 ]);

const MANIFEST = 'manifest.json';
const ARCHIVE_NAME = 'update.zip';
const ZIP_OPTIONS = '-mm=Deflate -mfb=258 -mpass=15 -r';

/**
 * @returns {Promise<void>}
 */
const compressUpdate = async () => {
    console.log('Compressing Update');
    await new Promise((resolve, reject) => {
        let sCommand = (
            '7z a -v' + sChunkSize + ' ' + ZIP_OPTIONS + ' ' +
            sUpdatePath + '/' + ARCHIVE_NAME + ' ' + sApkPath
        );

        let dir = exec(
            sCommand,
            (err) => {
                if (err != null) {
                    reject(err);
                }
            }
        );

        dir.on('exit', (code) => {
            if (code === 0) {
                resolve();
            }
        });
    });
};

/**
 * @param {string} sFilePath
 * @returns {Promise<string>}
 */
const getChecksum = async (sFilePath) => {
    return await new Promise((resolve) => {
        let rs = fs.createReadStream(sFilePath);
        let hash = crypto.createHash('md5');
        hash.setEncoding('hex');
        rs.on('end', () => {
            hash.end();
            resolve(hash.read());
        });
        rs.pipe(hash);
    });
};

/**
 * @param {string} sFilePath
 * @returns {Promise<number>}
 */
const getSize = async (sFilePath) => {
    return await new Promise((resolve, reject) => {
        fs.stat(sFilePath, (err, oStats) => {
            if (err == null) {
                resolve(oStats.size);
            } else {
                reject(err);
            }
        });
    });
};

/**
 * @returns {Promise<string[]>}
 */
const listChunks = async () => {
    return await new Promise((resolve, reject) => {
        fs.readdir(sUpdatePath, (err, aItems) => {
            if (err == null) {
                resolve(aItems);
            } else {
                reject(err);
            }
        });
    });
};

/**
 * @param {string[]} aChunks
 * @returns {Promise<{size: number, sums: string[]}>}
 */
const getChunkStats = async (aChunks) => {
    let nSize = 0;
    let aCheckSums = [];

    for (const sFileName of aChunks) {
        let sChunk = path.join(sUpdatePath, sFileName);
        aCheckSums.push(await getChecksum(sChunk));
        nSize += await getSize(sChunk);
    }

    return {sums: aCheckSums, size: nSize};
};

/**
 * @param {object} oManifest
 * @returns {Promise<void>}
 */
const writeManifestToFile = async (oManifest) => {
    return await new Promise((resolve, reject) => {
        fs.writeFile(
            path.join(sUpdatePath, MANIFEST),
            JSON.stringify(oManifest),
            (err) => {
                if (err != null) {
                    reject(err);
                } else {
                    resolve();
                }
            }
        );
    });
};

/**
 * @param {string} sPath
 * @returns {Promise<string[]>}
 */
const listFiles = async (sPath) => {
    return await new Promise((resolve, reject) => {
        fs.readdir(sPath, (err, aItems) => {
            if (err == null) {
                resolve(aItems);
            } else {
                reject(err);
            }
        });
    });
};

/**
 * @param {string[]} aFiles
 * @returns {Promise<void>}
 */
const removeUpdateFiles = async (aFiles) => {
    for (const sFile of aFiles) {
        await new Promise((resolve, reject) => {
            fs.unlink(path.join(sUpdatePath, sFile),
                (err) => {
                    if (err == null) {
                        resolve();
                    } else {
                        reject(err);
                    }
                }
            );
        });
    }
};

/**
 * @returns {Promise<void>}
 */
const prepareUpdateDirectory = async () => {
    if (fs.existsSync(sUpdatePath)) {
        // remove old update-files if they exist
        await removeUpdateFiles(await listFiles(sUpdatePath));
    } else {
        await new Promise((resolve, reject) => {
            fs.mkdir(sUpdatePath, (err) => {
                if (err == null) {
                    resolve();
                } else {
                    reject(err);
                }
            });
        });
    }
};

/**
 * @returns {Promise<void>}
 */
const createManifest = async () => {
    const oStats = await getChunkStats(await listChunks());
    await writeManifestToFile({
        version: sVersion,
        chunks: oStats.sums,
        compressedSize: oStats.size,
        size: await getSize(sApkPath),
        sum: await getChecksum(sApkPath)
    });
};

(createUpdate = async () => {
    try {
        await prepareUpdateDirectory();
        await compressUpdate();
        await createManifest();
    } catch (err) {
        console.error(err);
        process.exit(1);
    }
})();
