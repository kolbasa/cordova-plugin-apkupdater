/// <reference path="interfaces/App.d.ts" />
/// <reference path="interfaces/AuthConfig.d.ts" />
/// <reference path="interfaces/DownloadConfig.d.ts" />
/// <reference path="interfaces/InstallConfig.d.ts" />
/// <reference path="interfaces/ReadConfig.d.ts" />
/// <reference path="interfaces/Progress.d.ts" />
/// <reference path="interfaces/Update.d.ts" />
/// <reference path="interfaces/Version.d.ts" />

declare module 'cordova-plugin-apkupdater' {

    export class ApkUpdater {

        static getInstalledVersion(success?: Function, failure?: Function): Promise<App>;

        static download(updateUrl: string, config?: DownloadConfig, success?: Function, failure?: Function): Promise<Update>;

        static stop(success?: Function, failure?: Function): Promise<void>;

        static getDownloadedUpdate(options?: ReadConfig, success?: Function, failure?: Function): Promise<Update>;

        static reset(success?: Function, failure?: Function): Promise<void>;


        static canRequestPackageInstalls(success?: Function, failure?: Function): Promise<boolean>;

        static openInstallSetting(success?: Function, failure?: Function): Promise<boolean>;

        static isExternalStorageAuthorized(success?: Function, failure?: Function): Promise<boolean>;

        static requestExternalStorageAuthorization(success?: Function, failure?: Function): Promise<boolean>;

        static install(config?: InstallConfig, success?: Function, failure?: Function): Promise<void>;


        static isDeviceRooted(success?: Function, failure?: Function): Promise<boolean>;

        static requestRootAccess(success?: Function, failure?: Function): Promise<boolean>;

        static rootInstall(success?: Function, failure?: Function): Promise<void>;


        static isDeviceOwner(success?: Function, failure?: Function): Promise<boolean>;

        static ownerInstall(success?: Function, failure?: Function): Promise<void>;

    }

    export default ApkUpdater;

}
