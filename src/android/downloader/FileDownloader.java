package de.kolbasa.apkupdater.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import de.kolbasa.apkupdater.exceptions.DownloadFailedException;

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

    public File download(String fileUrl, File dir, String auth) throws DownloadFailedException {

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        File outputFile = new File(dir, fileName);

        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);

            if (auth != null) {
                connection.setRequestProperty("Authorization", auth);
            }

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

            InputStream is = connection.getInputStream();
            byte[] buffer = new byte[1024];

            int bytes;
            int bytesDownloaded = 0;
            long fileLength = connection.getContentLength();

            Progress progress = new Progress(fileLength);
            broadcast(progress);

            FileOutputStream fos = new FileOutputStream(outputFile);
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

        } catch (Exception err) {
            if (outputFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                outputFile.delete();
            }

            if (connection != null) {
                try {
                    int responseCode = connection.getResponseCode();
                    String responseMessage = connection.getResponseMessage();
                    if (responseMessage != null) {
                        throw new DownloadFailedException("{ response: " + "{ message: '" +
                                responseMessage + "', code: " + responseCode + " } }", err);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            throw new DownloadFailedException(err);
        } finally {
            interrupt();
        }

        return outputFile;
    }

}
