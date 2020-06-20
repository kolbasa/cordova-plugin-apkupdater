package de.kolbasa.apkupdater;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observer;

import de.kolbasa.apkupdater.downloader.FileTools;
import de.kolbasa.apkupdater.downloader.exceptions.AlreadyRunningException;
import de.kolbasa.apkupdater.downloader.exceptions.ManifestMissingException;
import de.kolbasa.apkupdater.downloader.update.UpdateDownloader;
import de.kolbasa.apkupdater.downloader.exceptions.WrongChecksumException;
import de.kolbasa.apkupdater.downloader.manifest.Manifest;
import de.kolbasa.apkupdater.downloader.manifest.ManifestDownloader;
import de.kolbasa.apkupdater.downloader.update.UpdateDownloadEvent;
import de.kolbasa.apkupdater.downloader.update.tools.UpdateValidator;

class UpdateManager {

    private static final int DEFAULT_TIMEOUT = 30 * 1000; // 30sec
    private static final String MANIFEST_FILE = "manifest.json";

    private int timeout;
    private String manifestUrl;
    private String downloadPath;
    private Observer observer;

    private Manifest manifest;
    private ManifestDownloader manifestDownloader;
    private UpdateDownloader updateDownloader;

    UpdateManager(String manifestUrl, String downloadPath, int timeout) {
        this.manifestUrl = validateUrl(manifestUrl);
        this.downloadPath = downloadPath;
        this.timeout = timeout;
        this.manifestDownloader = new ManifestDownloader(this.manifestUrl, this.downloadPath, this.timeout);
    }

    UpdateManager(String manifestUrl, String downloadPath) {
        this(manifestUrl, downloadPath, DEFAULT_TIMEOUT);
    }

    void addObserver(Observer observer) {
        this.observer = observer;
    }

    private String validateUrl(String manifestUrl) {
        if (manifestUrl.endsWith("/" + MANIFEST_FILE)) {
            manifestUrl = manifestUrl.replaceFirst("/" + MANIFEST_FILE, "");
        }
        return manifestUrl;
    }

    private void syncWithStorageFiles(Manifest manifest) throws IOException {
        manifest.setUpdateFile(UpdateValidator.findValidUpdate(manifest));
    }

    Manifest check() throws IOException, ParseException {
        manifest = manifestDownloader.download();
        syncWithStorageFiles(manifest);
        return manifest;
    }

    private void prepareDownload() throws ManifestMissingException, IOException,
            AlreadyRunningException {

        if (manifest == null) {
            throw new ManifestMissingException();
        }

        if (updateDownloader != null) {
            throw new AlreadyRunningException();
        }

        syncWithStorageFiles(manifest);

        updateDownloader = new UpdateDownloader(manifest, manifestUrl, downloadPath, timeout);

        if (observer != null) {
            updateDownloader.addObserver(observer);
        }

        updateDownloader.addObserver((o, arg) -> {
            if (arg instanceof UpdateDownloadEvent && arg.equals(UpdateDownloadEvent.STOPPED)) {
                updateDownloader.deleteObservers();
                updateDownloader = null;
            }
        });
    }

    void download() throws IOException, WrongChecksumException, ManifestMissingException,
            AlreadyRunningException {
        prepareDownload();
        updateDownloader.download();
    }

    void downloadInBackground(int intervalInMs) throws ManifestMissingException, IOException,
            AlreadyRunningException {
        prepareDownload();
        updateDownloader.download(intervalInMs);
    }

    void setDownloadInterval(int intervalInMs) {
        if (updateDownloader != null) {
            updateDownloader.setInterval(intervalInMs);
        }
    }

    boolean isDownloading() {
        if (updateDownloader != null) {
            return updateDownloader.isDownloading();
        }
        return false;
    }

    void stop() {
        if (manifestDownloader != null) {
            manifestDownloader.stop();
        }

        if (updateDownloader != null) {
            updateDownloader.stop();
            updateDownloader = null;
        }
    }

    void removeUpdates() {
        stop();
        FileTools.clearDirectory(new File(downloadPath));
    }

}
