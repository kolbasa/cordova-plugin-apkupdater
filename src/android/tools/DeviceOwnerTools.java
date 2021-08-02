package de.kolbasa.apkupdater.tools;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class DeviceOwnerTools {

    public static boolean isOwner(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) ((Activity) context).getSystemService(Context.DEVICE_POLICY_SERVICE);
        return mDPM.isDeviceOwnerApp(context.getPackageName());
    }

}
