package de.kolbasa.apkupdater.exceptions;

public class UnzipException extends Exception {
    public UnzipException(Exception originalException) {
        super("Unzip failed: " + originalException.getMessage(), originalException);
    }
}