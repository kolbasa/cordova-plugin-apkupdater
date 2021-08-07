package de.kolbasa.apkupdater.exceptions;

public class UpdateNotFoundException extends Exception {
    public UpdateNotFoundException(String path) {
        super("No valid update found in download directory: " + path);
    }
}