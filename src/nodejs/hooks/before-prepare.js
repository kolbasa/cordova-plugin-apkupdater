module.exports = function (context) {

    const cordovaUtils = context.requireCordovaModule('cordova-lib/src/cordova/util');

    (async () => {
        const platforms = await cordovaUtils.getInstalledPlatformsWithVersions(cordovaUtils.isCordova());
        if (platforms != null && platforms.android != null) {
            const cordovaAndroidVersion = parseInt(platforms.android.split('.')[0]);
            if (cordovaAndroidVersion < 9) {

                const color = (
                    '\x1b[41m' + // Red background
                    '\x1b[37m' + // White font
                    '\x1b[1m'    // Bright
                );

                const colorReset = '\x1b[0m';

                console.log(
                    '\n' +
                    color + 'Plugin: "cordova-plugin-apkupdater" can not be installed!' + colorReset +
                    '\n' +
                    color + '"cordova-android"-version too low. Expected > "9.0.0". Installed: "' + platforms.android + '"' + colorReset + '.' +
                    '\n'
                );

            }
        }
    })();

};