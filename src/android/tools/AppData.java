package de.kolbasa.apkupdater.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;

import de.kolbasa.apkupdater.exceptions.InvalidPackageException;

public class AppData {

    private final Context context;

    public AppData(Context context) {
        this.context = context;
    }

    private PackageManager getPackageManager() {
        return this.context.getPackageManager();
    }

    private PackageInfo getPackageInfo(File apk) throws PackageManager.NameNotFoundException, IOException, InvalidPackageException {
        PackageInfo info;
        if (apk == null) {
            info = getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
        } else {
            info = getPackageManager().getPackageArchiveInfo(apk.getAbsolutePath(), 0);

            if (info == null) {
                throw new InvalidPackageException(apk.getName() + " (size=" + apk.length() +
                        ", md5=" + ChecksumGenerator.getFileChecksum(apk) + ")");
            }
        }
        return info;
    }

    public String getPackageName(File apk) throws PackageManager.NameNotFoundException, IOException, InvalidPackageException {
        return getPackageInfo(apk).packageName;
    }

    public String getAppVersionName(File apk) throws PackageManager.NameNotFoundException, IOException, InvalidPackageException {
        return getPackageInfo(apk).versionName;
    }

    public Integer getAppVersionCode(File apk) throws PackageManager.NameNotFoundException, IOException, InvalidPackageException {
        return getPackageInfo(apk).versionCode;
    }

    public Long getFirstInstallTime() throws PackageManager.NameNotFoundException, IOException, InvalidPackageException {
        return getPackageInfo(null).firstInstallTime;
    }

    public String getAppName(File apk) throws PackageManager.NameNotFoundException, IOException, InvalidPackageException {
        return (String) getPackageManager().getApplicationLabel(getPackageInfo(apk).applicationInfo);
    }

}
