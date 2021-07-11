package de.kolbasa.apkupdater.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public class FileDownloader extends Observable {

    private static final int BROADCAST_LOCK_MILLIS = 100;

    private HttpURLConnection connection;

    public void interrupt() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    private void broadcast(Progress progress) {
        setChanged();
        notifyObservers(progress);
    }

    public File download(String fileUrl, File dir) throws IOException {

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        File outputFile = new File(dir, fileName);

        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();

            String headerFileName = connection.getHeaderField("Content-Disposition");
            if (headerFileName != null && headerFileName.contains("filename=\"")) {
                String name = headerFileName.split("\"")[1];
                if (name != null) {
                    outputFile = new File(dir, name);
                }
            }

            // noinspection ResultOfMethodCallIgnored
            outputFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is;
            try {
                is = connection.getInputStream();
            } catch (Exception err) {
                //noinspection ResultOfMethodCallIgnored
                outputFile.delete();
                throw err;
            }
            byte[] buffer = new byte[1024];

            int bytes;
            int bytesDownloaded = 0;
            long fileLength = connection.getContentLength();

            Progress progress = new Progress(fileLength);
            broadcast(progress);

            long startTimeMillis = 0;
            while ((bytes = is.read(buffer)) != -1) {
                bytesDownloaded += bytes;
                fos.write(buffer, 0, bytes);

                if ((System.currentTimeMillis() - startTimeMillis) > BROADCAST_LOCK_MILLIS) {
                    progress.setBytesWritten(bytesDownloaded);
                    broadcast(progress);
                    startTimeMillis = System.currentTimeMillis();
                }
            }

            progress.setBytesWritten(bytesDownloaded);
            broadcast(progress);

            fos.flush();
            fos.close();
            is.close();
        } finally {
            interrupt();
        }

        return outputFile;
    }

}
