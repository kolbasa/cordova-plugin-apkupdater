package de.kolbasa.apkupdater.tools;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

public class WindowStatus {

    public static boolean isWindowed(Context context) {
        return (((Activity) context).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0;
    }

}
