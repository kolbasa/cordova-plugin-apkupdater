package de.kolbasa.apkupdater.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import de.kolbasa.apkupdater.exceptions.PlatformNotSupportedException;

public class PermissionManager {

    public static boolean canRequestPackageInstalls(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            return true;
        }
    }

    public static void openInstallSetting(Context context) throws PlatformNotSupportedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse(String.format("package:%s", context.getPackageName())));
            context.startActivity(intent);
        } else {
            throw new PlatformNotSupportedException("Not supported on Android < 8");
        }
    }

}
