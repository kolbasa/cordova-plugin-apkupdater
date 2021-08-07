package de.kolbasa.apkupdater.exceptions;

public class InvalidPackageException extends Exception {
    public InvalidPackageException(String details) {
        super("Invalid package file: " + details + "");
    }
}