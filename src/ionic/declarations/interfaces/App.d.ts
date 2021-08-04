declare module 'cordova-plugin-apkupdater' {

    interface App {

        /**
         * App name.
         */
        name: string;

        /**
         * Package name.
         */
        package: string;

        /**
         * App version.
         */
        version: Version;

        /**
         * Unix timestamp;
         */
        firstInstallTime?: number;

    }

}
