package de.kolbasa.apkupdater.update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Update {

    private final File installFile;

    private final AppInfo appInfo;

    public Update(File installFile, AppInfo appInfo) {
        this.installFile = installFile;
        this.appInfo = appInfo;
    }

    public File getInstallFile() {
        return installFile;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();

        result.put("name", installFile.getName());
        result.put("path", installFile.getParent());
        result.put("size", installFile.length());

        if (appInfo != null) {
            result.put("app", appInfo.toJSON());
        }

        return result;
    }

}
