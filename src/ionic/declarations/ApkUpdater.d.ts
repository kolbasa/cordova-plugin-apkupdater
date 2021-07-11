/// <reference path="types.d.ts" />
/// <reference path="interfaces/Manifest.d.ts" />

declare module "cordova-plugin-apkupdater" {

    export class ApkUpdater {

        static install(success?: Function, failure?: Function): Promise<void>;

    }

    export default ApkUpdater;

}