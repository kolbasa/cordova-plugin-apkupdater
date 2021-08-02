const plugin = function () {
    return (<any>window).ApkUpdater;
};

export default class ApkUpdater {

    static getInstalledVersion() {
        let apkUpdater = plugin();
        return apkUpdater.getInstalledVersion.apply(apkUpdater, arguments);
    }

    static download() {
        let apkUpdater = plugin();
        return apkUpdater.download.apply(apkUpdater, arguments);
    }

    static getDownloadedUpdate() {
        let apkUpdater = plugin();
        return apkUpdater.getDownloadedUpdate.apply(apkUpdater, arguments);
    }

    static install() {
        let apkUpdater = plugin();
        return apkUpdater.install.apply(apkUpdater, arguments);
    }

    static rootInstall() {
        let apkUpdater = plugin();
        return apkUpdater.rootInstall.apply(apkUpdater, arguments);
    }

    static stop() {
        let apkUpdater = plugin();
        return apkUpdater.stop.apply(apkUpdater, arguments);
    }

    static reset() {
        let apkUpdater = plugin();
        return apkUpdater.reset.apply(apkUpdater, arguments);
    }

    static canRequestPackageInstalls() {
        let apkUpdater = plugin();
        return apkUpdater.canRequestPackageInstalls.apply(apkUpdater, arguments);
    }

    static openInstallSetting() {
        let apkUpdater = plugin();
        return apkUpdater.openInstallSetting.apply(apkUpdater, arguments);
    }

    static isDeviceRooted() {
        let apkUpdater = plugin();
        return apkUpdater.isDeviceRooted.apply(apkUpdater, arguments);
    }

}
