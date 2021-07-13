package de.kolbasa.apkupdater.cordova;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import de.kolbasa.apkupdater.exceptions.DownloadInProgressException;
import de.kolbasa.apkupdater.exceptions.DownloadNotRunningException;
import de.kolbasa.apkupdater.exceptions.InstallationFailedException;
import de.kolbasa.apkupdater.exceptions.UpdateNotFoundException;

public class StackExtractor {

    public static JSONObject format(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();
        stack = stack.replace("\t", "  ");

        CordovaError mapped = null;
        if (exception instanceof IOException) {
            mapped = CordovaError.DOWNLOAD_FAILED;
        } else if (exception instanceof InstallationFailedException) {
            mapped = CordovaError.INSTALLATION_FAILED;
        } else if (exception instanceof UpdateNotFoundException) {
            mapped = CordovaError.DOWNLOAD_DIRECTORY_EMPTY;
        } else if (exception instanceof DownloadInProgressException) {
            mapped = CordovaError.DOWNLOAD_IN_PROGRESS;
        } else if (exception instanceof DownloadNotRunningException) {
            mapped = CordovaError.DOWNLOAD_NOT_RUNNING;
        }

        JSONObject error = new JSONObject();
        try {
            String message = mapped != null ? mapped.getMessage() : exception.getMessage();
            error.put("message", message);
            error.put("stack", stack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return error;
    }

}
