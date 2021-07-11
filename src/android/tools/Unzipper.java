package de.kolbasa.apkupdater.tools;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;

import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;


import de.kolbasa.apkupdater.downloader.Progress;

public class Unzipper extends Observable {

    private static final int BROADCAST_LOCK_MILLIS = 50;

    private boolean interrupted;

    public void interrupt() {
        interrupted = true;
    }

    private void broadcast(Progress progress) {
        setChanged();
        notifyObservers(progress);
    }

    public void unzip(File file, String password) throws IOException {
        interrupted = false;

        int bytes;
        int bytesUnzipped = 0;

        LocalFileHeader header;
        byte[] readBuffer = new byte[4096];

        char[] passwordChars = null;
        if (password != null) {
            passwordChars = password.toCharArray();
        }

        long startTimeMillis = 0;

        InputStream inputStream = new FileInputStream(file);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, passwordChars)) {
            while ((header = zipInputStream.getNextEntry()) != null) {
                File extractedFile = new File(file.getParent(), header.getFileName());

                long size = header.getUncompressedSize();
                if (size == 0) {
                    size = file.length();
                }
                Progress progress = new Progress(size);
                broadcast(progress);

                try (OutputStream os = new FileOutputStream(extractedFile)) {
                    while ((bytes = zipInputStream.read(readBuffer)) != -1 && !interrupted) {
                        bytesUnzipped += bytes;
                        os.write(readBuffer, 0, bytes);

                        if ((System.currentTimeMillis() - startTimeMillis) > BROADCAST_LOCK_MILLIS) {
                            progress.setBytesWritten(bytesUnzipped);
                            broadcast(progress);
                            startTimeMillis = System.currentTimeMillis();
                        }
                    }
                    os.flush();

                    progress.setBytesWritten(bytesUnzipped);
                    broadcast(progress);
                }
            }
        }

    }

}
