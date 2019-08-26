package de.kolbasa.apkupdater.downloader.update.tools;

import java.io.File;
import java.io.IOException;

import de.kolbasa.apkupdater.downloader.FileTools;
import de.kolbasa.apkupdater.downloader.manifest.Manifest;

public class UpdateValidator {

    public static File findValidUpdate(Manifest manifest) throws IOException {
        String checkSum = manifest.getChecksum();
        File downloadDir = new File(manifest.getFile().getParentFile(), checkSum);

        if (downloadDir.exists()) {
            File apk = FileTools.findByFileType(downloadDir, "apk");
            if (apk != null && apk.exists()) {
                if (manifest.getChecksum().equals(ChecksumGenerator.getFileChecksum(apk))) {
                    return apk;
                }
            }
        }

        return null;
    }

}
