exports.defineManualTests = function (contentEl, createActionButton) {

    var UPDATE_URL = 'https://raw.githubusercontent.com/kolbasa/cordova-plugin-apkupdater-demo/master/update';

    createActionButton('apkupdater.check()', function () {
        cordova.plugins.apkupdater.setObserver(
            {
                downloadProgress: function (nPercentage, nBytes, nBytesWritten, nChunks, nChunksWritten) {
                    console.log('Downloading ' + nChunksWritten + ' of ' + nChunks + ' (' + nPercentage + "%)");
                },
                unzipProgress: function (nPercentage) {
                    console.log('Unzipping: ' + nPercentage + '%');
                },
                event: function (sEvent) {
                    console.log(sEvent);
                },
                exception: function (sMessage, stack) {
                    console.error(sMessage);
                    if (stack != null) {
                        console.error(stack);
                    }
                }
            }
        );

        cordova.plugins.apkupdater.check(
            UPDATE_URL,
            function (oResp) {
                console.log(JSON.stringify(oResp, ' ', 2));
            }
        );
    });

    createActionButton('apkupdater.download()', function () {
        cordova.plugins.apkupdater.download(
            function () {
                console.log('Update can be installed now.');
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('apkupdater.backgroundDownload()', function () {
        cordova.plugins.apkupdater.backgroundDownload(
            function () {
                console.log('Update can be installed now.');
            },
            function (oResp) {
                console.error(oResp);
            },
            5000, // Mobile speed
            50  // Wifi speed
        );
    });

    createActionButton('apkupdater.install()', function () {
        cordova.plugins.apkupdater.install(
            function (oResp) {
                //
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('apkupdater.stop()', function () {
        cordova.plugins.apkupdater.stop(
            function (oResp) {
                //
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

    createActionButton('apkupdater.reset()', function () {
        cordova.plugins.apkupdater.reset(
            function () {
                console.log('Reset successfully.');
            },
            function (oResp) {
                console.error(oResp);
            }
        );
    });

};
