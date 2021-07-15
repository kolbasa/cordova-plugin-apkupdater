package de.kolbasa.apkupdater.update;

import java.io.File;
import java.io.IOException;
import java.util.Observer;

import de.kolbasa.apkupdater.downloader.FileDownloader;
import de.kolbasa.apkupdater.exceptions.UpdateNotFoundException;
import de.kolbasa.apkupdater.tools.ChecksumGenerator;
import de.kolbasa.apkupdater.tools.FileTools;
import de.kolbasa.apkupdater.tools.Unzipper;

public class UpdateManager {

    private static final String APK = "apk";
    private static final String SUPPORTED_ARCHIVE = "zip";

    private final File downloadDirectory;
    private Observer downloadObserver;
    private Observer unzipObserver;

    private FileDownloader fileDownloader;
    private Unzipper unzipper;

    public UpdateManager(File destination) {
        downloadDirectory = destination;
        createDownloadDirectory();
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
        if (unzipper != null) {
            unzipper.interrupt();
        }
    }

    public void reset() {
        if (isDownloading()) {
            stop();
            try {
                Thread.sleep(100);
                FileTools.clearDirectory(downloadDirectory);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            FileTools.clearDirectory(downloadDirectory);
        }
    }

    private void createDownloadDirectory() {
        if (!downloadDirectory.exists()) {
            // noinspection ResultOfMethodCallIgnored
            downloadDirectory.mkdir();
        }
    }

    private File downloadFile(String path, String basicAuth) throws IOException {
        try {
            fileDownloader = new FileDownloader();
            if (downloadObserver != null) {
                fileDownloader.addObserver(downloadObserver);
            }
            return fileDownloader.download(path, downloadDirectory, basicAuth);
        } finally {
            fileDownloader = null;
            downloadObserver = null;
        }
    }

    private File unzipFile(File file, String password) throws IOException {
        if (!FileTools.isType(file, SUPPORTED_ARCHIVE)) {
            return null;
        }
        try {
            unzipper = new Unzipper();
            if (unzipObserver != null) {
                unzipper.addObserver(unzipObserver);
            }
            unzipper.unzip(file, password);
            File parentDir = file.getParentFile();
            assert parentDir != null;
            return FileTools.findByFileType(parentDir, APK);
        } finally {
            unzipper = null;
            unzipObserver = null;
        }
    }

    private Update wrap(File apk, File zip) throws IOException {
        if (apk == null) {
            return null;
        }
        return new Update(apk, zip, ChecksumGenerator.getFileChecksum(apk));
    }

    public Update getUpdate() throws IOException, UpdateNotFoundException {
        File update = FileTools.findByFileType(downloadDirectory, "apk");
        if (update == null) {
            throw new UpdateNotFoundException(downloadDirectory.getAbsolutePath());
        }
        return wrap(update, null);
    }

    public Update download(String path, String basicAuth, String zipPassword) throws IOException {
        File zip = downloadFile(path, basicAuth);
        File apk = unzipFile(zip, zipPassword);

        if (apk != null) {
            // noinspection ResultOfMethodCallIgnored
            zip.delete();
        } else {
            apk = zip;
            zip = null;
        }

        return wrap(apk, zip);
    }

    public boolean isDownloading() {
        return fileDownloader != null || unzipper != null;
    }
}
