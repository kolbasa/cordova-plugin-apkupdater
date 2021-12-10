declare module 'cordova-plugin-apkupdater' {

    interface Config {

        /**
         * If an encrypted zip file is used.
         */
        zipPassword?: string;

        /**
         * HTTP basic access authentication.
         */
        basicAuth?: AuthConfig;

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
