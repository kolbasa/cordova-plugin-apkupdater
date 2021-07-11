package de.kolbasa.apkupdater.cordova;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class CordovaEvent {

    private static final String PUSH_EVENT = "" +
            "javascript:" +
            "typeof window !== 'undefined' && " +
            "typeof window.ApkUpdater !== 'undefined' && " +
            "window.ApkUpdater._event";

    public static String format(String name, Map<String, String> params) {

        StringBuilder js = new StringBuilder();

        js.append(PUSH_EVENT).append("('").append(name).append("',{");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            js.append(entry.getKey()).append(":");
            if (!StringUtils.isNumeric(entry.getValue())) {
                js.append("'").append(entry.getValue()).append("'");
            } else {
                js.append(entry.getValue());
            }
            js.append(",");
        }
        js.setLength(js.length() - 1);

        js.append("})");

        return js.toString();
    }
}
