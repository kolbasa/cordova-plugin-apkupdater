package de.kolbasa.apkupdater;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import de.kolbasa.apkupdater.downloader.FileTools;
import de.kolbasa.apkupdater.downloader.exceptions.AlreadyRunningException;
import de.kolbasa.apkupdater.downloader.manifest.Manifest;
import de.kolbasa.apkupdater.downloader.exceptions.ManifestMissingException;
import de.kolbasa.apkupdater.downloader.progress.DownloadProgress;
import de.kolbasa.apkupdater.downloader.progress.UnzipProgress;
import de.kolbasa.apkupdater.downloader.exceptions.WrongChecksumException;
import de.kolbasa.apkupdater.downloader.update.UpdateDownloadEvent;

public class ApkUpdater extends CordovaPlugin {

    private static final String TAG = "ApkUpdater";
    private static final String UPDATE_DIR = "apk-update";

    private String downloadUrl;
    private Manifest manifest;
    private UpdateManager manager;

    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkListener;

    private static final int DEFAULT_SLOW_UPDATE_INTERVAL = 30 * 60 * 1000; // 1h

    private static final String CORDOVA_CHECK = "" +
            "javascript:" +
            "typeof cordova !== 'undefined' && " +
            "typeof cordova.plugins !== 'undefined' && " +
            "typeof cordova.plugins.apkupdater !== 'undefined' && " +
            "cordova.plugins.apkupdater._";

    private boolean notInitialized(CallbackContext callbackContext) {
        if (manager == null) {
            callbackContext.error(CordovaError.NOT_INITIALIZED.getMessage());
            return true;
        }
        return false;
    }

    private File getUpdateDirectory() {
        return new File(cordova.getContext().getFilesDir(), UPDATE_DIR);
    }

    private void checkForUpdate(JSONArray data, CallbackContext callbackContext) {
        try {
            String url = data.getString(0);

            // Download url changed
            if (manager != null && downloadUrl != null && !url.equals(downloadUrl)) {
                reset(null);
            }

            if (manager == null) {
                File updateDir = getUpdateDirectory();
                if (!updateDir.exists()) {
                    // noinspection ResultOfMethodCallIgnored
                    updateDir.mkdir();
                }
                downloadUrl = url;
                manager = new UpdateManager(url, updateDir.getAbsolutePath());
            }

            manifest = manager.check();
            JSONObject result = new JSONObject();

            File updateFile = manifest.getUpdateFile();
            result.put("version", manifest.getVersion());
            result.put("ready", updateFile != null);
            if (updateFile != null) {
                result.put("file", updateFile.getName());
            }
            result.put("size", manifest.getCompressedSize());
            result.put("chunks", manifest.getChunks().size());

            callbackContext.success(result);
        } catch (Exception e) {
            handleException(e, callbackContext);
        }
    }

    private void broadcastDownloadProgress(DownloadProgress progress) {
        cordova.getActivity().runOnUiThread(() -> webView.loadUrl("" +
                CORDOVA_CHECK +
                "downloadProgress(" +
                progress.getPercent() + ", " +
                progress.getBytes() + ", " +
                progress.getBytesWritten() + ", " +
                progress.getChunks() + ", " +
                progress.getChunksDownloaded() +
                ")"
        ));
    }

    private void broadcastUnzipProgress(UnzipProgress progress) {
        cordova.getActivity().runOnUiThread(() -> webView.loadUrl("" +
                CORDOVA_CHECK +
                "unzipProgress(" +
                progress.getPercent() + ", " +
                progress.getBytes() + ", " +
                progress.getBytesWritten() +
                ")"
        ));
    }

    private void broadcastException(String message, String stack) {
        cordova.getActivity().runOnUiThread(() -> webView.loadUrl(
                CORDOVA_CHECK + "exception('" + message + "', '" + stack + "')"
        ));
    }

    private void broadcastEvent(Event event) {
        cordova.getActivity().runOnUiThread(() -> webView.loadUrl(
                CORDOVA_CHECK + "event('" + event.getMessage() + "')"
        ));
    }

    private void handleException(Exception exception, CallbackContext callbackContext) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();

        CordovaError cordovaError = CordovaError.UNKNOWN_EXCEPTION;
        if (exception instanceof IOException) {
            cordovaError = CordovaError.DOWNLOAD_FAILED;
        } else if (exception instanceof ParseException) {
            cordovaError = CordovaError.INVALID_MANIFEST;
        } else if (exception instanceof WrongChecksumException) {
            cordovaError = CordovaError.WRONG_CHECKSUM;
        } else if (exception instanceof ManifestMissingException) {
            cordovaError = CordovaError.NOT_INITIALIZED;
        } else if (exception instanceof AlreadyRunningException) {
            cordovaError = CordovaError.DOWNLOAD_ALREADY_RUNNING;
        } else {
            exception.printStackTrace();
        }

        broadcastException(cordovaError.getMessage(), stack);
        if (callbackContext != null) {
            callbackContext.error(cordovaError.getMessage());
        }
    }

    private void handleException(Exception exception) {
        handleException(exception, null);
    }

    private void addObserver(UpdateManager manager, CallbackContext callbackContext) {
        manager.addObserver((o, arg) -> {
            if (arg instanceof DownloadProgress) {
                broadcastDownloadProgress((DownloadProgress) arg);
            }

            if (arg instanceof UnzipProgress) {
                broadcastUnzipProgress((UnzipProgress) arg);
            }

            if (arg instanceof Exception) {
                handleException((Exception) arg);
            }

            if (arg instanceof UpdateDownloadEvent) {
                UpdateDownloadEvent event = (UpdateDownloadEvent) arg;
                boolean downloadStopped = event.equals(UpdateDownloadEvent.STOPPED);

                if (callbackContext != null) {
                    if (event.equals(UpdateDownloadEvent.UPDATE_READY)) {
                        callbackContext.success();
                    } else if (downloadStopped && manifest.getUpdateFile() == null) {
                        callbackContext.error(CordovaError.UPDATE_NOT_READY.getMessage());
                    }
                }

                if (downloadStopped) {
                    unregisterConnectivityActionReceiver();
                }

                broadcastEvent(event);
            }
        });
    }

    private void download(CallbackContext callbackContext) {
        if (notInitialized(callbackContext)) {
            return;
        }

        try {
            if (manifest.getUpdateFile() != null) {
                callbackContext.success();
                return;
            }

            addObserver(manager, null);
            manager.download();

            if (manifest != null && manifest.getUpdateFile() != null) {
                callbackContext.success();
            } else {
                callbackContext.error(CordovaError.UPDATE_NOT_READY.getMessage());
            }
        } catch (Exception e) {
            handleException(e, callbackContext);
        }
    }

    private void adjustSpeed(int slowInterval) {
        if (manager == null) {
            return;
        }
        if (this.cm.isActiveNetworkMetered()) {
            manager.setDownloadInterval(slowInterval);
        } else {
            manager.setDownloadInterval(1);
        }
    }

    private void registerConnectivityActionReceiver(int slowInterval) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                this.cm = (ConnectivityManager) cordova.getContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                this.networkListener = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        adjustSpeed(slowInterval);
                    }

                    @Override
                    public void onLost(Network network) {
                        adjustSpeed(slowInterval);
                    }
                };
                this.cm.registerDefaultNetworkCallback(this.networkListener);
            } catch (Exception e) {
                //
            }
        }
    }

    private void unregisterConnectivityActionReceiver() {
        if (networkListener != null && cm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.unregisterNetworkCallback(networkListener);
            cm = null;
            networkListener = null;
        }
    }

    private void downloadInBackground(JSONArray data, CallbackContext callbackContext) {
        if (notInitialized(callbackContext)) {
            return;
        }

        try {
            if (manifest.getUpdateFile() != null) {
                callbackContext.success();
                return;
            }

            addObserver(manager, callbackContext);

            int slowInterval = DEFAULT_SLOW_UPDATE_INTERVAL;

            try {
                slowInterval = data.getInt(0);
            } catch (org.json.JSONException e) {
                //
            }

            manager.downloadInBackground(slowInterval);
            registerConnectivityActionReceiver(slowInterval);
        } catch (Exception e) {
            handleException(e, callbackContext);
        }
    }

    private void reset(CallbackContext callbackContext) {
        try {
            if (manager == null) {
                FileTools.clearDirectory(getUpdateDirectory());
            } else {
                manager.removeUpdates();
                manifest = null;
                manager = null;
            }

            if (callbackContext != null) {
                callbackContext.success();
            }
            unregisterConnectivityActionReceiver();
        } catch (Exception e) {
            e.printStackTrace();
            if (callbackContext != null) {
                callbackContext.error(e.toString());
            }
        }
    }

    private void stop(CallbackContext callbackContext) {
        if (notInitialized(callbackContext)) {
            return;
        }

        try {
            if (manager.isDownloading()) {
                manager.stop();
                callbackContext.success();
            } else {
                callbackContext.error(CordovaError.DOWNLOAD_NOT_RUNNING.getMessage());
            }
        } catch (Exception e) {
            handleException(e, callbackContext);
        }
    }

    private void install(CallbackContext callbackContext) {
        try {
            if (notInitialized(callbackContext)) {
                return;
            }

            File update = manifest.getUpdateFile();

            if (update == null) {
                callbackContext.error(CordovaError.UPDATE_NOT_READY.getMessage());
                return;
            }

            ApkInstaller installer = new ApkInstaller();
            installer.addObserver((o, arg) -> {
                if (arg instanceof ApkInstaller.InstallEvent) {
                    broadcastEvent((ApkInstaller.InstallEvent) arg);
                }
            });
            installer.install(cordova.getContext(), update);

            callbackContext.success();
        } catch (Exception e) {
            handleException(e, callbackContext);
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Log.v(TAG, "Executing (" + action + ")");

        switch (action) {
            case "check":
                cordova.getThreadPool().execute(() -> checkForUpdate(data, callbackContext));
                break;
            case "download":
                cordova.getThreadPool().execute(() -> download(callbackContext));
                break;
            case "backgroundDownload":
                cordova.getThreadPool().execute(() -> downloadInBackground(data, callbackContext));
                break;
            case "stop":
                cordova.getThreadPool().execute(() -> stop(callbackContext));
                break;
            case "reset":
                cordova.getThreadPool().execute(() -> reset(callbackContext));
                break;
            case "install":
                cordova.getThreadPool().execute(() -> install(callbackContext));
                break;
            default:
                return false;
        }

        return true;
    }

}
