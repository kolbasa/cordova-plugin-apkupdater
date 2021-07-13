var plugin = function () {
    return window.ApkUpdater;
};
var ApkUpdater = /** @class */ (function () {
    function ApkUpdater() {
    }
    ApkUpdater.install = function () {
        var apkUpdater = plugin();
        return apkUpdater.install.apply(apkUpdater, arguments);
    };
    return ApkUpdater;
}());
export default ApkUpdater;
