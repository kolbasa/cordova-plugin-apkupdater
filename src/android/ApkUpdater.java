package de.kolbasa.apkupdater;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.kolbasa.apkupdater.exceptions.DownloadInProgressException;
import de.kolbasa.apkupdater.exceptions.DownloadNotRunningException;
import de.kolbasa.apkupdater.exceptions.UpdateNotFoundException;
import de.kolbasa.apkupdater.tools.ApkInstaller;
import de.kolbasa.apkupdater.tools.AppData;
import de.kolbasa.apkupdater.downloader.Progress;
import de.kolbasa.apkupdater.cordova.CordovaEvent;
import de.kolbasa.apkupdater.cordova.StackExtractor;
import de.kolbasa.apkupdater.update.Update;
import de.kolbasa.apkupdater.update.UpdateManager;

public class ApkUpdater extends CordovaPlugin {

    private static final String UPDATE_DIR = "update";

    private UpdateManager updateManager;

    private void init() {
        if (updateManager == null) {
            File downloadDir = new File(cordova.getContext().getFilesDir(), UPDATE_DIR);
            updateManager = new UpdateManager(downloadDir);
        }
    }

    private void pushEvent(String name, Map<String, String> params) {
        String js = CordovaEvent.format(name, params);
        cordova.getActivity().runOnUiThread(() -> webView.loadUrl(js));
    }

    private Map<String, String> mapProgress(Progress progress) {
        return new HashMap<String, String>() {{
            put("progress", "" + progress.getPercent());
            put("bytes", "" + progress.getBytes());
            put("bytesWritten", "" + progress.getBytesWritten());
        }};
    }

    private void checkIfRunning() throws DownloadInProgressException {
        if (updateManager != null && updateManager.isDownloading()) {
            throw new DownloadInProgressException();
        }
    }

    private JSONObject getAppInfo() throws JSONException, PackageManager.NameNotFoundException {
        return getAppInfo(null);
    }

    private JSONObject getAppInfo(File apk) throws JSONException, PackageManager.NameNotFoundException {
        AppData app = new AppData(cordova.getActivity());
        JSONObject appInfo = new JSONObject();

        appInfo.put("name", app.getAppName(apk));
        appInfo.put("package", app.getPackageName(apk));

        if (apk == null) {
            appInfo.put("firstInstallTime", app.getFirstInstallTime());
        }

        JSONObject version = new JSONObject();
        version.put("code", app.getAppVersionCode(apk));
        version.put("name", app.getAppVersionName(apk));
        appInfo.put("version", version);
        return appInfo;
    }

    private JSONObject getInfo(Update update) throws JSONException, PackageManager.NameNotFoundException {
        JSONObject result = new JSONObject();
        if (update.getZip() != null) {
            result.put("zip", update.getZip().getName());
        }
        File apk = update.getApk();
        result.put("update", apk.getName());
        result.put("path", apk.getParent());
        result.put("size", apk.length());
        result.put("checksum", update.getChecksum());
        result.put("app", getAppInfo(apk));
        return result;
    }

    private Update getUpdate() throws DownloadInProgressException, IOException, UpdateNotFoundException {
        checkIfRunning();
        return updateManager.getUpdate();
    }

    private void addDownloadObserver() {
        updateManager.addDownloadObserver((o, arg) -> {
            if (arg instanceof Progress) {
                pushEvent("downloadProgress", mapProgress((Progress) arg));
            }
        });
    }

    private void addUnzipObserver() {
        updateManager.addUnzipObserver((o, arg) -> {
            if (arg instanceof Progress) {
                pushEvent("unzipProgress", mapProgress((Progress) arg));
            }
        });
    }

    private void download(JSONArray data, CallbackContext callbackContext) {
        try {
            checkIfRunning();
            String url = data.getString(0);
            JSONObject options = data.getJSONObject(1);

            if (options.has("addProgressObserver")) {
                addDownloadObserver();
            }

            if (options.has("addUnzipObserver")) {
                addUnzipObserver();
            }

            String password = options.has("password") ? options.getString("password") : null;
            Update update = updateManager.download(url, password);

            callbackContext.success(getInfo(update));
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    private void stop(CallbackContext callbackContext) {
        try {
            if (updateManager.isDownloading()) {
                reset(callbackContext);
            } else {
                throw new DownloadNotRunningException();
            }
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    private void install(CallbackContext callbackContext) {
        try {
            ApkInstaller.install(cordova.getContext(), getUpdate().getApk());
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    private void rootInstall(CallbackContext callbackContext) {
        try {
            ApkInstaller.rootInstall(cordova.getContext(), getUpdate().getApk());
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    private void reset(CallbackContext callbackContext) {
        try {
            updateManager.reset();
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    private void getInstalledVersion(CallbackContext callbackContext) {
        try {
            callbackContext.success(getAppInfo());
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    private void getDownloadedUpdate(CallbackContext callbackContext) {
        try {
            callbackContext.success(getInfo(getUpdate()));
        } catch (Exception e) {
            callbackContext.error(StackExtractor.format(e));
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        init();
        switch (action) {
            case "getInstalledVersion":
                cordova.getThreadPool().execute(() -> getInstalledVersion(callbackContext));
                break;
            case "download":
                cordova.getThreadPool().execute(() -> download(data, callbackContext));
                break;
            case "stop":
                cordova.getThreadPool().execute(() -> stop(callbackContext));
                break;
            case "getDownloadedUpdate":
                cordova.getThreadPool().execute(() -> getDownloadedUpdate(callbackContext));
                break;
            case "install":
                cordova.getThreadPool().execute(() -> install(callbackContext));
                break;
            case "rootInstall":
                cordova.getThreadPool().execute(() -> rootInstall(callbackContext));
                break;
            case "reset":
                cordova.getThreadPool().execute(() -> reset(callbackContext));
                break;
            default:
                return false;
        }

        return true;
    }

}