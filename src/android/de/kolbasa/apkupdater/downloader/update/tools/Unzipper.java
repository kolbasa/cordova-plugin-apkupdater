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
            ZipEntry chunk;
            while ((chunk = zis.getNextEntry()) != null && !interrupted) {
                File extract = new File(directoryPath, chunk.getName());
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(extract))) {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    int count = 0;
                    int bytesUnzipped = 0;

                    broadcastProgress(bytesUnzipped);

                    while ((bytes = zis.read(buffer, 0, 1024)) > -1 && !interrupted) {
                        bytesUnzipped += bytes;
                        os.write(buffer, 0, bytes);

                        // broadcast progress every 1Mb
                        if (++count % 1024 == 0) {
                            broadcastProgress(bytesUnzipped);
                        }
                    }

                    broadcastProgress(bytesUnzipped);

                    os.flush();
                }
            }
        }
    }

}
