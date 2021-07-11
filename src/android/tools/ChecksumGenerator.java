package de.kolbasa.apkupdater.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumGenerator {

    public static String getFileChecksum(File file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] byteBuffer = new byte[1024];
                for (int bytes; (bytes = fis.read(byteBuffer)) > -1; ) {
                    md.update(byteBuffer, 0, bytes);
                }
            }

            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
