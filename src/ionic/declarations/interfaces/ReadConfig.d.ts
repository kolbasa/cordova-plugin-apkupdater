declare module 'cordova-plugin-apkupdater' {

    interface ReadConfig {

        /**
         * Generates a MD5 checksum of the APK/XAPK.
         * Can become a performance problem depending on the file size.
         * Therefore, it is disabled by default.
         */
        generateChecksum?: boolean;

    }

}
