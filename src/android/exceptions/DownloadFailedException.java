package de.kolbasa.apkupdater.exceptions;

public class DownloadFailedException extends Exception {
    public DownloadFailedException(Exception originalException) {
        super("Download failed: " + originalException.getMessage(), originalException);
    }

    public DownloadFailedException(String message, Exception originalException) {
        super("Download failed: " + message, originalException);
    }
}