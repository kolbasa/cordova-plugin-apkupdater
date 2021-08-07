package de.kolbasa.apkupdater.exceptions;

public class DownloadNotRunningException extends Exception {
    public DownloadNotRunningException() {
        super("Download not running");
    }
}