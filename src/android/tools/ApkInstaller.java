package de.kolbasa.apkupdater.tools;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.WindowManager;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import de.kolbasa.apkupdater.exceptions.InstallationFailedException;
import de.kolbasa.apkupdater.exceptions.PlatformNotSupportedException;

public class ApkInstaller {

    private static boolean isNotFullscreen(Context context) {
        return (((Activity) context).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0;
    }

    private static Uri getUpdate(Context context, File update) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String fileProvider = context.getPackageName() + ".apkupdater.provider";
            return FileProvider.getUriForFile(context, fileProvider, update);
        } else {
            File externalPath = new File(context.getExternalCacheDir(), update.getName());
            FileTools.copy(update, externalPath);
            return Uri.fromFile(externalPath);
        }
    }

    public static void install(Context context, File update) throws IOException {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (isNotFullscreen(context)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setData(getUpdate(context, update));
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(getUpdate(context, update), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * @author Kevin Kowalewski
     * https://stackoverflow.com/questions/1101380
     */
    public static boolean isDeviceRooted() {

        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) {
                return true;
            }
        } catch (Exception e) {
            //
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return false;
    }

    /**
     * https://stackoverflow.com/a/39420232
     */
    public static boolean requestRootAccess() throws IOException {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = in.readLine();
            return output != null && output.toLowerCase().contains("uid=0");
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static void rootInstall(Context context, File update) throws InstallationFailedException, IOException, InterruptedException {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String mainActivity = launchIntent.getComponent().getClassName();

        // -r Reinstall if needed
        // -d Downgrade if needed
        String command = "pm install -r -d " + update.getAbsolutePath() +
                " && am start -n " + packageName + "/" + mainActivity;

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            StringBuilder builder = new StringBuilder();

            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = stdOut.readLine()) != null) {
                builder.append(s);
            }
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((s = stdError.readLine()) != null) {
                builder.append(s);
            }

            process.waitFor();
            stdOut.close();
            stdError.close();

            if (builder.length() > 0) {
                throw new InstallationFailedException(builder.toString());
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static boolean isDeviceOwner(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return mDPM.isDeviceOwnerApp(context.getPackageName());
    }

    public static void ownerInstall(Context context, File update) throws IOException {
        if (!isDeviceOwner(context)) {
            throw new SecurityException("App is not device owner");
        }

        InputStream in = context.getContentResolver().openInputStream(getUpdate(context, update));

        PackageManager pm = context.getPackageManager();
        PackageInstaller pi = pm.getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        // params.setAppPackageName(context.getPackageName());

        int sessionId = pi.createSession(params);
        PackageInstaller.Session s = pi.openSession(sessionId);
        OutputStream out = s.openWrite(update.getName(), 0, -1);
        byte[] buffer = new byte[65536];
        int chunk;
        while ((chunk = in.read(buffer)) != -1) {
            out.write(buffer, 0, chunk);
        }
        s.fsync(out);
        in.close();
        out.close();

        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName()); // Restart app after update
        PendingIntent pendingIntent = PendingIntent.getActivity(((Activity) context), 0, intent, flags);
        s.commit(pendingIntent.getIntentSender());
    }

    public static boolean canRequestPackageInstalls(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            // noinspection deprecation
            String name = Settings.Global.INSTALL_NON_MARKET_APPS;
            return Settings.Global.getInt(null, name, 0) == 1;
        }
    }

    public static void openInstallSetting(Context context) throws PlatformNotSupportedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse(String.format("package:%s", context.getPackageName())));
            if (isNotFullscreen(context)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            throw new PlatformNotSupportedException("SDK: " + Build.VERSION.SDK_INT);
        }
    }

}
