package de.kolbasa.apkupdater.downloader.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.kolbasa.apkupdater.downloader.FileDownloader;
import de.kolbasa.apkupdater.downloader.FileTools;
import de.kolbasa.apkupdater.downloader.progress.DownloadProgress;
import de.kolbasa.apkupdater.downloader.progress.UnzipProgress;
import de.kolbasa.apkupdater.downloader.manifest.Manifest;
import de.kolbasa.apkupdater.downloader.exceptions.WrongChecksumException;
import de.kolbasa.apkupdater.downloader.update.tools.ChecksumGenerator;
import de.kolbasa.apkupdater.downloader.update.tools.Unzipper;
import de.kolbasa.apkupdater.downloader.update.tools.UpdateValidator;

public class UpdateDownloader extends FileDownloader {

    private static final int BROADCAST_LOCK_MILLIS = 50;

    private Manifest manifest;
    private String serverURL;
    private String downloadPath;
    private int timeout;
    private UpdateChunk currentChunk;
    private Unzipper unzipper;

    private Timer timer;
    private DownloadProgress progress;
    private DownloadProgress lastProgress;

    private Integer interval;
    private boolean downloading;
    private int checksumFails;

    private long startTimeMillis;

    public UpdateDownloader(Manifest manifest, String serverUrl, String downloadPath, int timeout) {
        this.manifest = manifest;
        this.serverURL = serverUrl;
        this.downloadPath = downloadPath;
        this.timeout = timeout;
        this.progress = new DownloadProgress(
                manifest.getCompressedSize(),
                manifest.getChunks().size()
        );
    }

    public boolean isDownloading() {
        return downloading;
    }

    private void updateProgress() {
        int totalSize = 0;
        int downloadedChunks = 0;

        List<UpdateChunk> chunks = manifest.getChunks();
        for (int i = 0; i < chunks.size(); i++) {
            UpdateChunk chunk = chunks.get(i);
            totalSize += chunk.getBytesDownloaded();
            if (chunk.isReady()) {
                downloadedChunks++;
            }
        }

        progress.setBytesWritten(totalSize);
        progress.setChunksDownloaded(downloadedChunks);

        flowControlledProgressBroadcast();
    }

    @Override
    public void onProgress(int total, int current) {
        if (currentChunk == null) {
            return;
        }

        currentChunk.setBytesDownloaded(current);

        // Only interim updates will be broadcasted
        // If the download is ready it must be validated first
        if (total != current) {
            updateProgress();
        }
    }

    private void prepareDownload() {
        downloading = true;

        setChanged();
        notifyObservers(UpdateDownloadEvent.STARTING);

        String checkSum = manifest.getChecksum();
        File downloadDir = new File(downloadPath, checkSum);

        if (!downloadDir.exists()) {
            // noinspection ResultOfMethodCallIgnored
            downloadDir.mkdir();
        }

        for (File dir : new File(downloadPath).listFiles()) {
            if (dir.isDirectory() && !dir.getName().equals(checkSum)) {
                FileTools.deleteDirectory(dir);
            }
        }

        List<UpdateChunk> chunks = manifest.getChunks();
        for (int i = 0; i < chunks.size(); i++) {
            UpdateChunk chunk = chunks.get(i);
            String suffix = String.format(Locale.ENGLISH, "%03d", i + 1);
            File file = new File(downloadDir, "update.zip." + suffix);
            chunk.setFile(file);
        }
    }

    public void download() throws IOException, WrongChecksumException {
        try {
            if (downloading) {
                return;
            }

            prepareDownload();
            List<File> files = new ArrayList<>();

            for (UpdateChunk chunk : manifest.getChunks()) {
                files.add(chunk.getFile());
                if (!hasValidClone(chunk)) {
                    downloadChunk(chunk);
                }
            }

            currentChunk = null;
            unzip(files);
        } finally {
            stop();
        }
    }

    public void download(int interval) {
        prepareDownload();
        startTimer(interval);
    }

    public void stop() {
        boolean wasDownloading = downloading;
        downloading = false;

        interrupt(); // interrupt download

        stopTimer();

        if (unzipper != null) {
            unzipper.interrupt(); // interrupt unzipping
        }

        if (wasDownloading) {
            broadcast(UpdateDownloadEvent.STOPPED);
        }

        deleteObservers();
    }

    private void broadcast(Object event) {
        setChanged();
        notifyObservers(event);
    }

    private boolean shouldBroadcast() {
        if (lastProgress == null) {
            return true;
        }

        if (lastProgress.getChunksDownloaded() != progress.getChunksDownloaded()) {
            return true;
        }

        if (lastProgress.getPercent() == progress.getPercent()) {
            return false;
        }

        if (progress.getPercent() == 0 || progress.getPercent() == 100) {
            return true;
        }

        return (System.currentTimeMillis() - startTimeMillis) > BROADCAST_LOCK_MILLIS;
    }

    private void flowControlledProgressBroadcast() {
        if (shouldBroadcast()) {
            startTimeMillis = System.currentTimeMillis();
            lastProgress = new DownloadProgress(progress);
            broadcast(lastProgress);
        }
    }

    private boolean hasValidClone(UpdateChunk chunk) throws IOException {
        File file = chunk.getFile();

        if (file.exists()) {
            if (chunk.getChecksum().equals(ChecksumGenerator.getFileChecksum(file))) {
                chunk.setReady(true);
                chunk.setBytesDownloaded(file.length());
                return true;
            } else {
                chunk.setReady(false); // mark as not ready
                chunk.setBytesDownloaded(0);
                // noinspection ResultOfMethodCallIgnored
                file.delete(); // delete corrupted file
            }
        }

        return false;
    }

    private void downloadChunk(UpdateChunk chunk)
            throws IOException, WrongChecksumException {

        if (!downloading || currentChunk != null) {
            return;
        }

        try {
            currentChunk = chunk;

            File file = chunk.getFile();

            String url = serverURL + "/" + file.getName();

            super.download(url, file.getParent(), timeout);

            if (!hasValidClone(chunk) && downloading) {
                throw new WrongChecksumException(file.getName());
            }

            updateProgress();
        } finally {
            currentChunk = null;
        }
    }

    private void unzip(List<File> files) throws IOException {
        File update = UpdateValidator.findValidUpdate(manifest);
        if (!downloading || update != null) {
            return; // already extracted
        }

        unzipper = new Unzipper();

        UnzipProgress progress = new UnzipProgress(manifest.getSize());
        UpdateDownloader _this = this;
        unzipper.addObserver((o, arg) -> {
            if (arg instanceof Integer) {
                progress.setBytesWritten((int) arg);
                _this.broadcast(new UnzipProgress(progress));
            }
        });

        unzipper.unzip(files);

        update = UpdateValidator.findValidUpdate(manifest);
        manifest.setUpdateFile(update);
        if (update != null) {
            broadcast(UpdateDownloadEvent.UPDATE_READY);
        }
    }

    private void startNativeTimer(int interval) {
        timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (downloadNextChunk()) {
                    stop();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, interval, interval);
    }

    private void startTimer(int interval) {
        this.interval = interval;
        startNativeTimer(interval);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void setInterval(int interval) {
        if (downloading && (this.interval == null || this.interval != interval)) {
            if (this.interval != null) {
                setChanged();
                if (this.interval > interval) {
                    notifyObservers(UpdateDownloadEvent.SPEEDING_UP_DOWNLOAD);
                } else {
                    notifyObservers(UpdateDownloadEvent.SLOWING_DOWN_DOWNLOAD);
                }
            }

            stopTimer();
            startTimer(interval);
        }
    }

    private boolean downloadNextChunk() {
        List<File> files = new ArrayList<>();

        try {
            List<UpdateChunk> chunks = manifest.getChunks();
            for (UpdateChunk chunk : chunks) {
                if (hasValidClone(chunk)) {
                    files.add(chunk.getFile());
                } else {
                    downloadChunk(chunk);
                    files.add(chunk.getFile());
                    checksumFails = 0;
                    break;
                }
            }

            if (files.size() == chunks.size()) {
                unzip(files);
                return true;
            }
        } catch (FileNotFoundException e) {
            broadcast(e);
            stop(); // UpdateChunk is missing on server
        } catch (IOException e) {
            broadcast(e);
        } catch (WrongChecksumException e) {
            broadcast(e);
            if (++checksumFails > 2) {
                stop();
            }
        } catch (Exception e) {
            broadcast(e);
            stop();
        }

        return false;
    }
}
