package de.kolbasa.apkupdater.update;

import java.io.File;

public class Update {

    private final File apk;

    private final String checksum;

    public Update(File apk, String checksum) {
        this.apk = apk;
        this.checksum = checksum;
    }

    public File getApk() {
        return apk;
    }

    public String getChecksum() {
        return checksum;
    }
}
