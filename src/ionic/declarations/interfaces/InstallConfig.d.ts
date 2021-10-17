declare module 'cordova-plugin-apkupdater' {

    interface InstallConfig {

        /**
         * Monitor unzip progress.
         */
        onUnzipProgress?: (progress: Progress) => void;

    }

}
