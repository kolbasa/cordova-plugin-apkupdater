package de.kolbasa.apkupdater.tools;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.progress.ProgressMonitor;

import de.kolbasa.apkupdater.downloader.Progress;

public class ArchiveManager extends Observable {

    private static final int BROADCAST_LOCK_MILLIS = 50;

    private void broadcast(Progress progress) {
        setChanged();
        notifyObservers(progress);
    }

    public void extract(File archive, Map<String, File> files, String password) throws Exception {

        ZipFile zipFile = new ZipFile(archive, password == null ? null : password.toCharArray());
        zipFile.setRunInThread(true);
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        if (files == null) {
            files = new HashMap<>();
            for (FileHeader header : zipFile.getFileHeaders()) {
                files.put(header.getFileName(), new File(archive.getParentFile(), header.getFileName()));
            }
        }

        long size = 0;
        for (Map.Entry<String, File> entry : files.entrySet()) {
            size += zipFile.getFileHeader(entry.getKey()).getUncompressedSize();
        }

        Progress progress = new Progress(size, true);

        long currentSize = 0;
        for (Map.Entry<String, File> entry : files.entrySet()) {

            FileHeader header = zipFile.getFileHeader(entry.getKey());
            long fileSize = header.getUncompressedSize();

            zipFile.extractFile(header, entry.getValue().getParent(), entry.getValue().getName());

            while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                progress.setBytesWritten(currentSize + (long) Math.ceil(fileSize / 100f * progressMonitor.getPercentDone()));
                broadcast(progress);
                //noinspection BusyWait
                Thread.sleep(BROADCAST_LOCK_MILLIS);
            }

            currentSize += fileSize;

            if (progressMonitor.getResult().equals(ProgressMonitor.Result.ERROR)) {
                throw progressMonitor.getException();
            }
        }

        progress.setBytesWritten(currentSize);
        broadcast(progress);

    }

}
