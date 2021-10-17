declare module 'cordova-plugin-apkupdater' {

    interface DownloadConfig {

        /**
         * If an encrypted zip file is used.
         */
        zipPassword?: string;

        /**
         * HTTP basic access authentication.
         */
        basicAuth?: AuthConfig;

        /**
         * Generates a MD5 checksum of the APK/XAPK.
         * Can become a performance problem depending on the file size.
         * Therefore, it is disabled by default.
         */
        generateChecksum?: boolean;

        /**
         * Monitor download progress.
         */
        onDownloadProgress?: (progress: Progress) => void;

        /**
         * Monitor unzip progress.
         */
        onUnzipProgress?: (progress: Progress) => void;

    }

}
