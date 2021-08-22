package de.kolbasa.apkupdater.exceptions;

public class RootException extends Exception {
    public RootException(Exception originalException) {
        super("Root command failed: " + originalException.getMessage(), originalException);
    }
}