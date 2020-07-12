package de.kolbasa.apkupdater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

import de.kolbasa.apkupdater.downloader.FileTools;

class ApkInstaller extends Observable {

    enum InstallEvent implements Event {
        COPYING("Copying update to cache"),
        INSTALLING("Installing update");

        private String readableString;

        @Override
        public String getMessage() {
            return this.readableString;
        }

        InstallEvent(String readableString) {
            this.readableString = readableString;
        }
    }

    void install(Context context, File update) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String fileProvider = context.getPackageName() + ".apkupdater.provider";
            setChanged();
            notifyObservers(InstallEvent.INSTALLING);
            intent.setData(FileProvider.getUriForFile(context, fileProvider, update));
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                File newPath = new File(context.getExternalCacheDir(), update.getName());
                setChanged();
                notifyObservers(InstallEvent.COPYING);
                FileTools.copy(update, newPath);
                setChanged();
                notifyObservers(InstallEvent.INSTALLING);
                intent.setDataAndType(Uri.fromFile(newPath), "application/vnd.android.package-archive");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        context.startActivity(intent);
    }

}
