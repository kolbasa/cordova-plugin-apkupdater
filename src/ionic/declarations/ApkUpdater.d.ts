/// <reference path="interfaces/App.d.ts" />
/// <reference path="interfaces/Config.d.ts" />
/// <reference path="interfaces/Progress.d.ts" />
/// <reference path="interfaces/Update.d.ts" />
/// <reference path="interfaces/Version.d.ts" />

declare module 'cordova-plugin-apkupdater' {

    export class ApkUpdater {

        static getInstalledVersion(success?: Function, failure?: Function): Promise<App>;

        static download(updateUrl: string, config?: Config, success?: Function, failure?: Function): Promise<Update>;

        static stop(success?: Function, failure?: Function): Promise<void>;

        static getDownloadedUpdate(success?: Function, failure?: Function): Promise<Update>;

        static reset(success?: Function, failure?: Function): Promise<void>;


        static canRequestPackageInstalls(success?: Function, failure?: Function): Promise<boolean>;

        static openInstallSetting(success?: Function, failure?: Function): Promise<boolean>;

        static install(success?: Function, failure?: Function): Promise<void>;


        static isDeviceRooted(success?: Function, failure?: Function): Promise<boolean>;

        static requestRootAccess(success?: Function, failure?: Function): Promise<boolean>;

        static rootInstall(success?: Function, failure?: Function): Promise<void>;


        static isDeviceOwner(success?: Function, failure?: Function): Promise<boolean>;

        static ownerInstall(success?: Function, failure?: Function): Promise<void>;

    }

    export default ApkUpdater;

}
