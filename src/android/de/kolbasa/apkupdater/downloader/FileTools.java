package de.kolbasa.apkupdater.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileTools {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void delete(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }

        if (fileToDelete.isDirectory()) {
            File[] files = fileToDelete.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    delete(file);
                } else {
                    file.delete();
                }
            }
        }

        fileToDelete.delete();
    }

    public static void deleteDirectory(File directory) {
        delete(directory);
    }

    public static void clearDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                delete(file);
            }
        }
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static File findByFileType(File directory, String type) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith("." + type)) {
                    return file;
                }
            }
        }
        return null;
    }

}
