package de.kolbasa.apkupdater.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import de.kolbasa.apkupdater.exceptions.PlatformNotSupportedException;

public class PermissionManager {

    private static final int PERMISSION_REQUEST_CODE = 55433;

    public static boolean hasWritePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestWritePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity) context).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

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

    public static void restartApp(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        Intent mainIntent = Intent.makeRestartActivityTask(intent.getComponent());
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}
