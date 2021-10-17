package de.kolbasa.apkupdater.exceptions;

public class ActionInProgressException extends Exception {
    public ActionInProgressException() {
        super("This action is already in progress");
    }
}