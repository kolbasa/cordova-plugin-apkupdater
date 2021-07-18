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

        String mapped = null;
        if (exception instanceof IOException) {
            if (stack.contains("\"su\"")) {
                mapped = "Device not rooted";
            } else {
                mapped = "Download failed";
            }
        } else if (exception instanceof InstallationFailedException) {
            mapped = "Could not install the update";
        } else if (exception instanceof UpdateNotFoundException) {
            mapped = "Download directory is empty";
        } else if (exception instanceof DownloadInProgressException) {
            mapped = "Download is in progress";
        } else if (exception instanceof DownloadNotRunningException) {
            mapped = "Download not running";
        }

        JSONObject error = new JSONObject();
        try {
            String message = mapped != null ? mapped : exception.getMessage();
            error.put("message", message);
            error.put("stack", stack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return error;
    }

}
