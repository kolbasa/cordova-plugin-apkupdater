package de.kolbasa.apkupdater.downloader.exceptions;

public class WrongChecksumException extends Exception {
    public WrongChecksumException(String errorMessage) {
        super(errorMessage);
    }
}

