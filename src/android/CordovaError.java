package de.kolbasa.apkupdater;

public enum CordovaError {

    DOWNLOAD_FAILED("Download failed"),
    WRONG_CHECKSUM("Checksum failed"),
    NOT_INITIALIZED("Manifest file is missing. Call \"check\" first."),
    INVALID_MANIFEST("Invalid manifest format"),
    UNKNOWN_EXCEPTION("Unknown exception"),
    UPDATE_NOT_READY("Update not ready"),
    DOWNLOAD_NOT_RUNNING("Download is not running."),
    DOWNLOAD_ALREADY_RUNNING("Download is already running.");

    private String readableString;

    public String getMessage() {
        return this.readableString;
    }

    CordovaError(String readableString) {
        this.readableString = readableString;
    }
}
