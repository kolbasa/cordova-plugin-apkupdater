package de.kolbasa.apkupdater.update;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import de.kolbasa.apkupdater.downloader.FileDownloader;
import de.kolbasa.apkupdater.exceptions.DownloadFailedException;
import de.kolbasa.apkupdater.exceptions.InvalidPackageException;
import de.kolbasa.apkupdater.exceptions.MissingPermissionsException;
import de.kolbasa.apkupdater.exceptions.UnzipException;
import de.kolbasa.apkupdater.exceptions.UpdateNotFoundException;
import de.kolbasa.apkupdater.tools.AppData;
import de.kolbasa.apkupdater.tools.ChecksumGenerator;
import de.kolbasa.apkupdater.tools.FileTools;
import de.kolbasa.apkupdater.tools.PermissionManager;
import de.kolbasa.apkupdater.tools.ArchiveManager;

public class UpdateManager {

    private static final String APK = "apk";
    private static final String ZIP = "zip";
    private static final String BUNDLE = "xapk";

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

    private File downloadFile(String path, String basicAuth) throws DownloadFailedException {
        try {
            fileDownloader = new FileDownloader();
            if (downloadObserver != null) {
                fileDownloader.addObserver(downloadObserver);
            }
            return fileDownloader.download(path, downloadDir, basicAuth);
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
            archiveManager.extract(file, null, password);
            // noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (Exception e) {
            throw new UnzipException(e);
        } finally {
            archiveManager = null;
        }
    }

    public void unzipBundle(Update update) throws UnzipException, IOException {
        if (update == null || update.getBundle() == null) {
            return;
        }

        for (Map.Entry<String, File> entry : update.getUpdateData().entrySet()) {
            File parentDir = entry.getValue().getParentFile();
            if (parentDir != null && !parentDir.getCanonicalPath().equals(downloadDir.getCanonicalPath())) {
                FileTools.delete(parentDir);
            }
        }

        File[] exemptions = new File[]{update.getBundle(), new File(downloadDir, MANIFEST)};
        FileTools.clearDirectory(downloadDir, exemptions);

        try {
            ArchiveManager archiveManager = new ArchiveManager();
            if (unzipObserver != null) {
                archiveManager.addObserver(unzipObserver);
            }

            if (update.getUpdateData().size() > 1) {
                if (!PermissionManager.hasWritePermission(context)) {
                    throw new MissingPermissionsException("External storage writing permission not granted");
                }

                if (!PermissionManager.canRequestPackageInstalls(context)) {
                    throw new MissingPermissionsException("Package installation permission not granted");
                }
            }

            archiveManager.extract(update.getBundle(), update.getUpdateData(), null);
        } catch (Exception e) {
            throw new UnzipException(e);
        }
    }

    private JSONObject getManifest(File src) throws IOException, JSONException {
        InputStream is = new FileInputStream(src);
        int size = is.available();
        byte[] buffer = new byte[size];
        //noinspection ResultOfMethodCallIgnored
        is.read(buffer);
        is.close();
        return new JSONObject(new String(buffer, StandardCharsets.UTF_8));
    }

    private static final String MANIFEST = "manifest.json";
    private static final String MANIFEST_APKS = "split_apks";
    private static final String MANIFEST_OBB_FILES = "expansions";
    private static final String MANIFEST_FILE = "file";
    private static final String MANIFEST_DESTINATION = "install_path";
    private static final String MANIFEST_NAME = "name";
    private static final String MANIFEST_PACKAGE_NAME = "package_name";
    private static final String MANIFEST_VERSION_NAME = "version_name";
    private static final String MANIFEST_VERSION_CODE = "version_code";

    private void unzipManifest() throws UnzipException, InvalidPackageException {
        List<File> bundles = FileTools.findByFileType(downloadDir, BUNDLE);

        if (bundles.size() > 1) {
            throw new InvalidPackageException("Multiple xapk-bundles found");
        }

        if (bundles.isEmpty()) {
            return;
        }

        try {
            archiveManager = new ArchiveManager();
            archiveManager.extract(bundles.get(0), new HashMap<String, File>() {{
                put(MANIFEST, new File(downloadDir, MANIFEST));
            }}, null);
        } catch (Exception e) {
            throw new UnzipException(e);
        } finally {
            archiveManager = null;
        }
    }

    private Update getBundleInfo(File manifest, boolean calcSum) throws JSONException,
            IOException, InvalidPackageException {

        File bundle = FileTools.findByFileType(downloadDir, BUNDLE).get(0);

        JSONObject obj = getManifest(manifest);

        JSONArray installFiles = obj.getJSONArray(MANIFEST_APKS);
        if (installFiles.length() > 1) {
            throw new InvalidPackageException("Split apks are note supported yet");
        }

        Map<String, File> data = new HashMap<>();
        if (obj.has(MANIFEST_OBB_FILES)) {
            JSONArray expansions = obj.getJSONArray(MANIFEST_OBB_FILES);
            for (int i = 0; i < expansions.length(); i++) {
                JSONObject entry = expansions.getJSONObject(i);
                File destination = new File(Environment.getExternalStorageDirectory(), entry.getString(MANIFEST_DESTINATION));
                data.put(entry.getString(MANIFEST_FILE), destination);
            }
        }

        String updatePath = installFiles.getJSONObject(0).getString(MANIFEST_FILE);
        File update = new File(downloadDir, updatePath);
        data.put(updatePath, update);

        String sum = calcSum ? ChecksumGenerator.getFileChecksum(bundle) : null;

        String name = obj.getString(MANIFEST_NAME);
        String packageName = obj.getString(MANIFEST_PACKAGE_NAME);
        String versionName = obj.getString(MANIFEST_VERSION_NAME);
        Integer versionCode = obj.getInt(MANIFEST_VERSION_CODE);
        AppInfo info = new AppInfo(name, packageName, versionName, versionCode, null);

        return new Update(update, sum, info, bundle, data);
    }

    private Update getApkInfo(boolean calcSum) throws UpdateNotFoundException, IOException,
            InvalidPackageException, PackageManager.NameNotFoundException {

        List<File> updateFiles = FileTools.findByFileType(downloadDir, APK);

        if (updateFiles.size() > 1) {
            throw new InvalidPackageException("Split apks are note supported yet");
        }

        if (updateFiles.isEmpty()) {
            throw new UpdateNotFoundException(downloadDir.getCanonicalPath());
        }

        File update = updateFiles.get(0);
        String sum = calcSum ? ChecksumGenerator.getFileChecksum(update) : null;
        AppInfo info = AppData.getPackageInfo(context, update);

        return new Update(update, sum, info);
    }

    public Update getUpdate(boolean calcSum) throws IOException, UpdateNotFoundException, JSONException,
            InvalidPackageException, PackageManager.NameNotFoundException {
        File bundleManifest = new File(downloadDir, MANIFEST);
        if (bundleManifest.exists()) {
            return getBundleInfo(bundleManifest, calcSum);
        }
        return getApkInfo(calcSum);
    }

    public Update download(String path, String basicAuth, String zipPassword, boolean calcSum) throws IOException,
            UnzipException, DownloadFailedException, JSONException, UpdateNotFoundException,
            InvalidPackageException, PackageManager.NameNotFoundException {

        try {
            reset();
            File downloadedFile = downloadFile(path, basicAuth);

            unzipUpdate(downloadedFile, zipPassword);
            unzipManifest();

            return getUpdate(calcSum);
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
