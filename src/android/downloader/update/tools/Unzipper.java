package de.kolbasa.apkupdater.downloader.update.tools;

import java.io.File;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper extends Observable {

    private static final String LOCK_MARKER = ".lock";

    private static final int BROADCAST_LOCK_MILLIS = 50;

    private boolean interrupted;

    public void interrupt() {
        interrupted = true;
    }

    private void broadcastProgress(int bytesUnzipped) {
        setChanged();
        notifyObservers(bytesUnzipped);
    }

    public void unzip(List<File> chunks) throws IOException {
        interrupted = false;

        ArrayList<FileInputStream> chunkToExtract = new ArrayList<>();
        String directoryPath = null;
        for (File chunk : chunks) {
            if (directoryPath == null) {
                directoryPath = chunk.getParent();
            }
            chunkToExtract.add(new FileInputStream(chunk));
        }

        SequenceInputStream sis = new SequenceInputStream(Collections.enumeration(chunkToExtract));

        try (ZipInputStream zis = new ZipInputStream(sis)) {

            File lockFile = null;
            ZipEntry apk;

            int bytesUnzipped = 0;
            long startTimeMillis = 0;

            while ((apk = zis.getNextEntry()) != null && !interrupted) {
                File extract = new File(directoryPath, apk.getName());

                if (lockFile == null) {
                    lockFile = new File(directoryPath, apk.getName() + LOCK_MARKER);
                    //noinspection ResultOfMethodCallIgnored
                    lockFile.createNewFile();
                }

                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(extract))) {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    broadcastProgress(bytesUnzipped);

                    while ((bytes = zis.read(buffer, 0, 1024)) > -1 && !interrupted) {
                        bytesUnzipped += bytes;
                        os.write(buffer, 0, bytes);

                        if ((System.currentTimeMillis() - startTimeMillis) > BROADCAST_LOCK_MILLIS) {
                            broadcastProgress(bytesUnzipped);
                            startTimeMillis = System.currentTimeMillis();
                        }
                    }

                    broadcastProgress(bytesUnzipped);

                    os.flush();
                }
            }

            if (lockFile != null && lockFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                lockFile.delete();
            }
        }
    }

}
