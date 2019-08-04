package de.kolbasa.apkupdater.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public abstract class FileDownloader extends Observable {

    public abstract void onProgress(int total, int current);

    private boolean interrupted;

    protected void interrupt() {
        interrupted = true;
    }

    protected void download(String fileUrl, String destination, int timeout) throws IOException {
        interrupted = false;

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();

        File file = new File(destination);

        File outputFile = new File(file, fileName);
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
        int count = 0;
        int bytesDownloaded = 0;
        int fileLength = connection.getContentLength();

        this.onProgress(fileLength, bytesDownloaded);

        while ((bytes = is.read(buffer)) != -1 && !interrupted) {
            bytesDownloaded += bytes;
            fos.write(buffer, 0, bytes);

            // broadcast progress every 20kb
            if (++count % 20 == 0) {
                this.onProgress(fileLength, bytesDownloaded);
            }
        }

        this.onProgress(fileLength, bytesDownloaded);

        fos.flush();
        fos.close();
        is.close();
        connection.disconnect();
    }

}
