/// <reference path="interfaces/App.d.ts" />
/// <reference path="interfaces/AuthConfig.d.ts" />
/// <reference path="interfaces/Config.d.ts" />
/// <reference path="interfaces/Progress.d.ts" />
/// <reference path="interfaces/Update.d.ts" />
/// <reference path="interfaces/Version.d.ts" />

declare module 'cordova-plugin-apkupdater' {

    export class ApkUpdater {

        static getInstalledVersion(success?: Function, failure?: Function): Promise<App>;

        static download(updateUrl: string, config?: Config, success?: Function, failure?: Function): Promise<Update>;

        static getDownloadedUpdate(success?: Function, failure?: Function): Promise<Update>;

        static install(success?: Function, failure?: Function): Promise<void>;

        static rootInstall(success?: Function, failure?: Function): Promise<void>;

        static stop(success?: Function, failure?: Function): Promise<void>;

        static reset(success?: Function, failure?: Function): Promise<void>;

        static canRequestPackageInstalls(success?: Function, failure?: Function): Promise<boolean>;

        static openInstallSetting(success?: Function, failure?: Function): Promise<boolean>;

    }

    export default ApkUpdater;

}
