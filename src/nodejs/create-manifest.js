const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const exec = require('child_process').exec;

const aArguments = process.argv.slice(2);
if (aArguments.length < 4) {
    console.error(
        '\nUSAGE: \n' +
        '\tcreate-manifest.js <version> <chunk-size> <apk-path> <output-path>\n\n' +
        '\tsize:        the size of one compressed chunk: {size}[b|k|m], e.g. 500b, 150k, 1m\n' +
        '\tversion:     a string of your choosing, will not be used by the plugin\n' +
        '\tapk-path:    the path to the apk-file\n' +
        '\toutput-path: the path to copy the zip-files to'
    );
    process.exit(1);
}

let sVersion = aArguments[ 0 ];
let sChunkSize = aArguments[ 1 ];
let sApkPath = aArguments[ 2 ];
let sUpdatePath = aArguments[ 3 ];

function compressUpdate() {
    let sCommand = (
        '7z a -v' + sChunkSize + ' -mm=Deflate -mfb=258 -mpass=15 -r '
        + sUpdatePath + '/' + sVersion + '.zip ' + sApkPath
    );

    console.log('Compressing Update');
    return new Promise(function (resolve, reject) {
        let dir = exec(
            sCommand,
            function (err) {
                if (err != null) {
                    reject(err);
                }
            }
        );
        dir.on('exit', function (code) {
            if (code === 0) {
                resolve();
            }
        });
    });
}

function getChecksum(sFilePath) {
    return new Promise(function (resolve) {
        let rs = fs.createReadStream(sFilePath);
        let hash = crypto.createHash('md5');
        hash.setEncoding('hex');
        rs.on('end', function () {
            hash.end();
            resolve(hash.read());
        });
        rs.pipe(hash);
    });
}

function getSize(sFilePath) {
    return new Promise(function (resolve, reject) {
        fs.stat(sFilePath, function (err, oStats) {
            if (err == null) {
                resolve(oStats.size);
            } else {
                reject(err);
            }
        });
    });
}

function listChunks() {
    return new Promise(function (resolve, reject) {
        fs.readdir(sUpdatePath, function (err, items) {
            if (err == null) {
                resolve(items);
            } else {
                reject(err);
            }
        });
    });
}

function getChunkStats(aChunks) {
    let promise = Promise.resolve();
    let nSize = 0;
    let aCheckSums = [];

    aChunks.forEach(function (sFileName) {
        let sChunk = path.join(sUpdatePath, sFileName);
        promise = promise
            .then(function () {
                return getChecksum(sChunk);
            })
            .then(function (sHash) {
                aCheckSums.push(sHash);
                return getSize(sChunk);
            })
            .then(function (_nSize) {
                nSize += _nSize;
            });
    });

    return promise.then(function () {
        return [ aCheckSums, nSize ];
    });
}

function writeManifestToFile() {
    return new Promise(function (resolve, reject) {
        fs.writeFile(
            path.join(sUpdatePath, 'manifest.json'),
            JSON.stringify(oManifest),
            function (err) {
                if (err != null) {
                    reject(err);
                } else {
                    resolve();
                }
            }
        );
    });
}

function listFiles(sPath) {
    return new Promise(function (resolve, reject) {
        fs.readdir(sPath, function (err, aItems) {
            if (err == null) {
                resolve(aItems);
            } else {
                reject(err);
            }
        });
    });
}

function removeUpdateFiles(aFiles) {
    let promise = Promise.resolve();
    aFiles.forEach(function (sFile) {
        promise = promise
            .then(function () {
                return new Promise(function (resolve, reject) {
                    fs.unlink(path.join(sUpdatePath, sFile),
                        function (err) {
                            if (err == null) {
                                resolve();
                            } else {
                                reject(err);
                            }
                        }
                    );
                });
            });
    });
}

function prepareUpdateDirectory() {
    if (fs.existsSync(sUpdatePath)) {
        // remove old update-files if they exist
        return listFiles(sUpdatePath).then(removeUpdateFiles);
    } else {
        return new Promise(function (resolve, reject) {
            fs.mkdir(sUpdatePath, function (err) {
                if (err == null) {
                    resolve();
                } else {
                    reject(err);
                }
            });
        });
    }
}

let oManifest = {
    version: sVersion,
    sum: null,
    size: null,
    compressedSize: null,
    chunks: []
};

prepareUpdateDirectory()
    .then(compressUpdate)
    .then(listChunks)
    .then(getChunkStats)
    .then(function (aStats) {
        oManifest.chunks = aStats[ 0 ];
        oManifest.compressedSize = aStats[ 1 ];
        return getSize(sApkPath);
    })
    .then(function (nSize) {
        oManifest.size = nSize;
        return getChecksum(sApkPath);
    })
    .then(function (sChecksum) {
        oManifest.sum = sChecksum;
        return writeManifestToFile();
    })
    .catch(function (err) {
        console.error(err);
        process.exit(1);
    });
