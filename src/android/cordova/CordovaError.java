package de.kolbasa.apkupdater.cordova;

public enum CordovaError {

    DOWNLOAD_FAILED("Download failed"),
    DOWNLOAD_DIRECTORY_EMPTY("Download directory is empty."),
    DOWNLOAD_IN_PROGRESS("Download is in progress."),
    DOWNLOAD_NOT_RUNNING("Download not running."),
    INSTALLATION_FAILED("Could not install the update.");

    private final String readableString;

    public String getMessage() {
        return this.readableString;
    }

    CordovaError(String readableString) {
        this.readableString = readableString;
    }
}
