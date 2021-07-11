package de.kolbasa.apkupdater.tools;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

public class AppData {

    private final Activity activity;

    public AppData(Activity activity) {
        this.activity = activity;
    }

    private PackageManager getPackageManager() {
        return this.activity.getPackageManager();
    }

    private PackageInfo getPackageInfo(File apk) throws PackageManager.NameNotFoundException {
        PackageInfo info;
        if (apk == null) {
            info = getPackageManager().getPackageInfo(this.activity.getPackageName(), 0);
        } else {
            info = getPackageManager().getPackageArchiveInfo(apk.getAbsolutePath(), 0);
        }
        return info;
    }

    public String getPackageName(File apk) throws PackageManager.NameNotFoundException {
        return getPackageInfo(apk).packageName;
    }

    public String getAppVersionName(File apk) throws PackageManager.NameNotFoundException {
        return getPackageInfo(apk).versionName;
    }

    public Integer getAppVersionCode(File apk) throws PackageManager.NameNotFoundException {
        return getPackageInfo(apk).versionCode;
    }

    public Long getFirstInstallTime() throws PackageManager.NameNotFoundException {
        return getPackageInfo(null).firstInstallTime;
    }

    public String getAppName(File apk) throws PackageManager.NameNotFoundException {
        return (String) getPackageManager().getApplicationLabel(getPackageInfo(apk).applicationInfo);
    }

}
