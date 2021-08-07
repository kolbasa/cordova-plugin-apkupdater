package de.kolbasa.apkupdater.exceptions;

public class UnzipException extends Exception {
    public UnzipException(Exception e) {
        super("Unzip failed: " + e.getMessage() + " (" + e.getClass().getName() + ")");
        super.setStackTrace(e.getStackTrace());
    }
}