package de.kolbasa.apkupdater.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackExtractor {

    public static JSONObject format(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();
        stack = stack.replace("\t", "  ");

        JSONObject error = new JSONObject();
        try {
            String message = exception.getMessage();
            if (message != null) {
                stack = stack.replace(": " + message, "");
            }

            error.put("message", message);
            error.put("stack", stack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return error;
    }

}
