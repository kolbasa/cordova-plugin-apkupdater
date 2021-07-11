declare module "cordova-plugin-apkupdater" {

    interface Manifest {

        version: string;

        sum: string;

        size: number;

        chunks: string[];

    }

}