package de.kolbasa.apkupdater.update;

import java.io.File;

public class Update {

    private final File apk;

    private final File zip;

    private final String checksum;

    public Update(File apk, File zip, String checksum) {
        this.apk = apk;
        this.zip = zip;
        this.checksum = checksum;
    }

    public File getApk() {
        return apk;
    }

    public File getZip() {
        return zip;
    }

    public String getChecksum() {
        return checksum;
    }
}
