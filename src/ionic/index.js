var plugin = function () {
    return window.ApkUpdater;
};
var ApkUpdater = /** @class */ (function () {
    function ApkUpdater() {
    }
    ApkUpdater.getInstalledVersion = function () {
        var apkUpdater = plugin();
        return apkUpdater.getInstalledVersion.apply(apkUpdater, arguments);
    };
    ApkUpdater.download = function () {
        var apkUpdater = plugin();
        return apkUpdater.download.apply(apkUpdater, arguments);
    };
    ApkUpdater.getDownloadedUpdate = function () {
        var apkUpdater = plugin();
        return apkUpdater.getDownloadedUpdate.apply(apkUpdater, arguments);
    };
    ApkUpdater.install = function () {
        var apkUpdater = plugin();
        return apkUpdater.install.apply(apkUpdater, arguments);
    };
    ApkUpdater.rootInstall = function () {
        var apkUpdater = plugin();
        return apkUpdater.rootInstall.apply(apkUpdater, arguments);
    };
    ApkUpdater.stop = function () {
        var apkUpdater = plugin();
        return apkUpdater.stop.apply(apkUpdater, arguments);
    };
    ApkUpdater.reset = function () {
        var apkUpdater = plugin();
        return apkUpdater.reset.apply(apkUpdater, arguments);
    };
    ApkUpdater.canRequestPackageInstalls = function () {
        var apkUpdater = plugin();
        return apkUpdater.canRequestPackageInstalls.apply(apkUpdater, arguments);
    };
    ApkUpdater.openInstallSetting = function () {
        var apkUpdater = plugin();
        return apkUpdater.openInstallSetting.apply(apkUpdater, arguments);
    };
    return ApkUpdater;
}());
export default ApkUpdater;
