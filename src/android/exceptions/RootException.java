package de.kolbasa.apkupdater.exceptions;

public class RootException extends Exception {
    public RootException(Exception e) {
        super("Root command failed: " + e.getMessage() + " (" + e.getClass().getName() + ")");
        super.setStackTrace(e.getStackTrace());
    }
}