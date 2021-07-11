const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const archiver = require('archiver');

const args = process.argv.slice(2);
if (args.length < 3) {
    console.error(
        '\nUSAGE: \n' +
        '\tcompress-apk.js <version> <apk-path> <output-path> [<password>]\n\n' +
        '\tversion:     a string of your choosing, it will not be used by the plugin\n' +
        '\tapk-path:    the path to the apk-file\n' +
        '\toutput-path: the path to which the update should be copied\n' +
        '\tpassword:    optional password\n'
    );
    process.exit(1);
}

const version = args[0];
const apkPath = path.resolve(args[1]);
let updatePath = path.resolve(args[2]);
const password = args[3];

let zipPath;
if (updatePath.endsWith('.zip')) {
    zipPath = updatePath;
    updatePath = path.parse(updatePath).dir;
} else {
    zipPath = path.join(updatePath, path.parse(apkPath).name + '.zip');
}

/**
 * @returns {Promise<void>}
 */
const compressUpdate = async () => {
    process.stdout.write('Compressing update' + (password == null ? '' : ' with password') + '... ');

    if (password != null) {
        archiver.registerFormat('zip-encryptable', require('archiver-zip-encryptable'));
    }

    let output = fs.createWriteStream(zipPath);

    let archive = archiver(
        password == null ? 'zip' : 'zip-encryptable',
        {
            zlib: {
                level: 9
            },
            password: password
        }
    );

    archive.pipe(output);
    archive.append(fs.createReadStream(apkPath), {name: path.parse(apkPath).base});

    await archive.finalize();
    console.log('done.');
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
 * @param {object} oManifest
 * @returns {Promise<void>}
 */
const writeManifestToFile = async (oManifest) => {
    return await new Promise((resolve, reject) => {
        fs.writeFile(
            path.join(updatePath, 'manifest.json'),
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
            fs.unlink(path.join(updatePath, sFile),
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
    if (fs.existsSync(updatePath)) {
        // remove old update-files if they exist
        await removeUpdateFiles(await listFiles(updatePath));
    } else {
        await new Promise((resolve, reject) => {
            fs.mkdir(updatePath, (err) => {
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
    await writeManifestToFile({
        version: version,
        size: await getSize(apkPath),
        compressedSize: await getSize(zipPath),
        sum: await getChecksum(apkPath)
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
