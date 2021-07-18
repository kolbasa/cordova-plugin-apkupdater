declare module 'cordova-plugin-apkupdater' {

    interface Progress {

        /**
         * Flaoting point.
         */
        progress: number;

        bytes: number;

        bytesWritten: number;

    }

}
