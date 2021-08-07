package de.kolbasa.apkupdater.exceptions;

public class DownloadFailedException extends Exception {
    public DownloadFailedException(Exception e) {
        super("Download failed: " + e.getMessage() + " (" + e.getClass().getName() + ")");
        super.setStackTrace(e.getStackTrace());
    }
}