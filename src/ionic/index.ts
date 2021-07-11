const plugin = function () {
    return (<any>window).ApkUpdater;
};

export default class ApkUpdater {

    static install() {
        let apkUpdater = plugin();
        return apkUpdater.install.apply(apkUpdater, arguments);
    }

}