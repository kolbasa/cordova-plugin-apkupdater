declare module 'cordova-plugin-apkupdater' {

    interface Update {

        /**
         * File name.
         */
        name: string;

        /**
         * File path.
         */
        path: string;

        /**
         * File size.
         */
        size: number;

        /**
         * Unix timestamp.
         */
        firstInstallTime?: number;

        /**
         * MD5 Checksum.
         */
        checksum: string;

        /**
         * App details.
         */
        app: App;

    }

}
