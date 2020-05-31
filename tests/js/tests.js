exports.defineManualTests = function (contentEl, createActionButton) {

    var UPDATE_URL = 'https://github.com/kolbasa/cordova-plugin-apkupdater/tree/demo/update';

    createActionButton('Check', function () {
        cordova.plugins.apkupdater.setObserver(
            {
                downloadProgress: function (nPercentage, nBytes, nBytesWritten, nChunks, nChunksWritten) {
                    console.log('Download: ' + nPercentage + ' (' + nChunksWritten + '/' + nChunks + ')');
                },
                unzipProgress: function (nPercentage) {
                    console.log('Unzipping: ' + nPercentage);
                },
                event: function (sEvent) {
                    console.log(sEvent);
                },
                exception: function (sMessage) {
                    console.error(sMessage);
                }
            }
        );

        cordova.plugins.apkupdater.check(
            UPDATE_URL + '/real/1.0.0',
            function (oResp) {
                console.log(oResp);
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('Reset ApkUpdater', function () {
        cordova.plugins.apkupdater.reset(
            function (oResp) {
                console.log(oResp);
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('Download', function () {
        cordova.plugins.apkupdater.download(
            function (oResp) {
                console.log('Update can be installed.');
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('Background download', function () {
        cordova.plugins.apkupdater.backgroundDownload(
            function (oResp) {
                console.log('Update can be installed.');
            },
            function (oResp) {
                console.error(oResp);
            },
            60000,
            100
        );
    });

    createActionButton('Install', function () {
        cordova.plugins.apkupdater.install(
            function (oResp) {
                //
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('Stop download', function () {
        cordova.plugins.apkupdater.stop(
            function (oResp) {
                //
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

};
