package de.kolbasa.apkupdater.update;

import org.json.JSONException;
import org.json.JSONObject;

public class AppInfo {

    private final String name;

    private final String packageName;

    private final String appVersionName;

    private final Integer appVersionCode;

    private final Long firstInstallTime;

    public AppInfo(String name, String packageName, String appVersionName, Integer appVersionCode, Long firstInstallTime) {
        this.name = name;
        this.packageName = packageName;
        this.appVersionName = appVersionName;
        this.appVersionCode = appVersionCode;
        this.firstInstallTime = firstInstallTime;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject appInfo = new JSONObject();

        appInfo.put("name", name);
        appInfo.put("package", packageName);

        if (firstInstallTime != null) {
            appInfo.put("firstInstallTime", firstInstallTime);
        }

        JSONObject version = new JSONObject();
        version.put("name", appVersionName);
        version.put("code", appVersionCode);
        appInfo.put("version", version);
        return appInfo;
    }

    public String getPackageName() {
        return packageName;
    }
}
