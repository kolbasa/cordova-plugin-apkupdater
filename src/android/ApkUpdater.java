package de.kolbasa.apkupdater;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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
    private BroadcastReceiver receiver;

    private static final int DEFAULT_SLOW_UPDATE_INTERVAL = 30 * 60 * 1000; // 1h

    private static final String CORDOVA_CHECK = "" +
            "javascript:" +
            "typeof cordova !== 'undefined' && " +
            "typeof cordova.plugins !== 'undefined' && " +
            "typeof cordova.plugins.apkupdater !== 'undefined' && " +
            "cordova.plugins.apkupdater._";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        this.cm = (ConnectivityManager) cordova.getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private boolean notInitialized(CallbackContext callbackContext) {
        if (manager == null) {
            callbackContext.error(CordovaError.NOT_INITIALIZED.getMessage());
            return true;
        }
        return false;
    }

    private void checkForUpdate(JSONArray data, CallbackContext callbackContext) {
        try {
            String url = data.getString(0);

            File storageDir = cordova.getContext().getFilesDir();

            // Download url changed
            if (manager != null && downloadUrl != null && !url.equals(downloadUrl)) {
                reset(null);
            }

            if (manager == null) {
                File updateDir = new File(storageDir, UPDATE_DIR);
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

            if (manifest.getUpdateFile() != null) {
                callbackContext.success();
            } else {
                callbackContext.error(CordovaError.UPDATE_NOT_READY.getMessage());
            }
        } catch (Exception e) {
            handleException(e, callbackContext);
        }
    }

    private void unregisterConnectivityActionReceiver() {
        if (receiver != null) {
            webView.getContext().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void registerConnectivityActionReceiver(int slowInterval) {
        unregisterConnectivityActionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    NetworkInfo network = cm.getActiveNetworkInfo();

                    int interval = slowInterval;
                    if (network != null) {
                        // connected to the internet
                        if (network.getType() == ConnectivityManager.TYPE_WIFI) {
                            interval = 1;
                        }
                    }

                    manager.setDownloadInterval(interval);
                } catch (Exception e) {
                    handleException(e);
                }
            }
        };
        webView.getContext().registerReceiver(receiver, intentFilter);
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
            manifest = null;

            if (manager != null) {
                manager.stop();
                manager.removeUpdates();
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
