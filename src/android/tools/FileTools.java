package de.kolbasa.apkupdater.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileTools {

    public static void delete(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }

        if (fileToDelete.isDirectory()) {
            File[] files = fileToDelete.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        delete(file);
                    } else {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        }

        //noinspection ResultOfMethodCallIgnored
        fileToDelete.delete();
    }

    public static void clearDirectory(File directory) throws IOException {
        clearDirectory(directory, null);
    }

    public static void clearDirectory(File directory, File[] exemptions) throws IOException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean isExempt = false;
                    if (exemptions != null) {
                        for (File exemption : files) {
                            if (file.getCanonicalPath().equals(exemption.getCanonicalPath())) {
                                isExempt = true;
                                break;
                            }
                        }
                    }

                    if (!isExempt) {
                        delete(file);
                    }
                }
            }
        }
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static boolean isType(File file, String type) {
        return file.getName().toLowerCase().endsWith("." + type);
    }

    public static List<File> findByFileType(File directory, String type) {
        List<File> filtered = new ArrayList<>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isType(file, type)) {
                        filtered.add(file);
                    }
                }
            }
        }
        return filtered;
    }

}
