package de.kolbasa.apkupdater.update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

public class Update {

    private final File update;

    private final AppInfo appInfo;

    private final File bundle;

    private final Map<String, File> updateData;

    public Update(File update, AppInfo appInfo) {
        this(update, appInfo, null, null);
    }

    public Update(File update, AppInfo appInfo, File bundle, Map<String, File> updateData) {
        this.update = update;
        this.appInfo = appInfo;
        this.bundle = bundle;
        this.updateData = updateData;
    }

    public File getUpdate() {
        return update;
    }

    public File getBundle() {
        return bundle;
    }

    public Map<String, File> getUpdateData() {
        return updateData;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();

        result.put("name", update.getName());
        result.put("path", update.getParent());
        result.put("size", update.length());

        if (appInfo != null) {
            result.put("app", appInfo.toJSON());
        }

        return result;
    }

}
