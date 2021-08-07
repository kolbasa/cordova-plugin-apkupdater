package de.kolbasa.apkupdater.exceptions;

public class DownloadInProgressException extends Exception {
    public DownloadInProgressException() {
        super("Download is in progress");
    }
}