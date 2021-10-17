package de.kolbasa.apkupdater.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;

import de.kolbasa.apkupdater.exceptions.InvalidPackageException;
import de.kolbasa.apkupdater.update.AppInfo;

public class AppData {

    public static AppInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException, InvalidPackageException, IOException {
        return getPackageInfo(context, null);
    }

    public static AppInfo getPackageInfo(Context context, File apk) throws PackageManager.NameNotFoundException, InvalidPackageException, IOException {
        if (context == null) {
            return null;
        }

        PackageManager packageManager = context.getPackageManager();

        PackageInfo info;
        if (apk == null) {
            info = packageManager.getPackageInfo(context.getPackageName(), 0);
        } else {
            info = packageManager.getPackageArchiveInfo(apk.getCanonicalPath(), 0);
            if (info == null) {
                throw new InvalidPackageException(apk.getName() + " (size=" + apk.length() + ")");
            }
        }

        String name = (String) packageManager.getApplicationLabel(info.applicationInfo);

        return new AppInfo(name, info.packageName, info.versionName, info.versionCode, info.firstInstallTime);
    }

}
