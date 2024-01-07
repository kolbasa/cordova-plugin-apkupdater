package de.kolbasa.apkupdater.update;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observer;

import de.kolbasa.apkupdater.downloader.FileDownloader;
import de.kolbasa.apkupdater.exceptions.DownloadFailedException;
import de.kolbasa.apkupdater.exceptions.InvalidPackageException;
import de.kolbasa.apkupdater.exceptions.UnzipException;
import de.kolbasa.apkupdater.exceptions.UpdateNotFoundException;
import de.kolbasa.apkupdater.tools.AppData;
import de.kolbasa.apkupdater.tools.ArchiveManager;
import de.kolbasa.apkupdater.tools.FileTools;

public class UpdateManager {

    private static final String APK = "apk";
    private static final String ZIP = "zip";

    private final File downloadDir;
    private Observer downloadObserver;
    private Observer unzipObserver;

    private FileDownloader fileDownloader;
    private ArchiveManager archiveManager;
    private final Context context;

    public UpdateManager(File downloadDirectory, Context context) {
        this.downloadDir = downloadDirectory;
        this.context = context;

        if (!downloadDir.exists()) {
            // noinspection ResultOfMethodCallIgnored
            downloadDir.mkdir();
        }
    }

    public void addDownloadObserver(Observer observer) {
        this.downloadObserver = observer;
    }

    public void addUnzipObserver(Observer observer) {
        this.unzipObserver = observer;
    }

    private void stop() {
        if (fileDownloader != null) {
            fileDownloader.interrupt();
        }
    }

    public void reset() throws IOException {
        if (isDownloading()) {
            stop();
            try {
                Thread.sleep(100);
                FileTools.clearDirectory(downloadDir);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            FileTools.clearDirectory(downloadDir);
        }
    }

    private File downloadFile(String path, String auth) throws DownloadFailedException {
        try {
            fileDownloader = new FileDownloader();
            if (downloadObserver != null) {
                fileDownloader.addObserver(downloadObserver);
            }
            return fileDownloader.download(path, downloadDir, auth);
        } finally {
            fileDownloader = null;
        }
    }

    private void unzipUpdate(File file, String password) throws UnzipException {
        if (!FileTools.isType(file, ZIP)) {
            return;
        }
        try {
            archiveManager = new ArchiveManager();
            if (unzipObserver != null) {
                archiveManager.addObserver(unzipObserver);
            }
            archiveManager.extract(file, password);
            // noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (Exception e) {
            throw new UnzipException(e);
        } finally {
            archiveManager = null;
        }
    }

    private Update getApkInfo() throws UpdateNotFoundException, IOException,
            InvalidPackageException, PackageManager.NameNotFoundException {

        List<File> updateFiles = FileTools.findByFileType(downloadDir, APK);

        if (updateFiles.size() > 1) {
            throw new InvalidPackageException("Split apks are not supported");
        }

        if (updateFiles.isEmpty()) {
            throw new UpdateNotFoundException(downloadDir.getCanonicalPath());
        }

        File update = updateFiles.get(0);
        AppInfo info = AppData.getPackageInfo(context, update);

        return new Update(update, info);
    }

    public Update getUpdate() throws IOException, UpdateNotFoundException,
            InvalidPackageException, PackageManager.NameNotFoundException {
        return getApkInfo();
    }

    public Update download(String path, String auth, String zipPassword) throws IOException,
            UnzipException, DownloadFailedException, UpdateNotFoundException,
            InvalidPackageException, PackageManager.NameNotFoundException {

        try {
            reset();

            File downloadedFile = downloadFile(path, auth);
            unzipUpdate(downloadedFile, zipPassword);

            return getUpdate();
        } catch (Exception e) {
            try {
                reset();
            } catch (Exception _e) {
                //
            }
            throw e;
        } finally {
            downloadObserver = null;
            unzipObserver = null;
        }

    }

    public boolean isDownloading() {
        return fileDownloader != null || archiveManager != null;
    }
}
