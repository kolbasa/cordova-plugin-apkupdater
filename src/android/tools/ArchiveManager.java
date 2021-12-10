package de.kolbasa.apkupdater.tools;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;
import java.util.Observable;

import de.kolbasa.apkupdater.downloader.Progress;

public class ArchiveManager extends Observable {

    private static final int BROADCAST_LOCK_MILLIS = 50;

    private void broadcast(Progress progress) {
        setChanged();
        notifyObservers(progress);
    }

    public void extract(File archive, String password) throws Exception {

        ZipFile zipFile = new ZipFile(archive, password == null ? null : password.toCharArray());
        zipFile.setRunInThread(true);
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        long size = archive.length();

        Progress progress = new Progress(size, true);

        zipFile.extractAll(archive.getParent());

        while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
            progress.setBytesWritten((long) Math.ceil(size / 100f * progressMonitor.getPercentDone()));
            broadcast(progress);
            //noinspection BusyWait
            Thread.sleep(BROADCAST_LOCK_MILLIS);
        }

        if (progressMonitor.getResult().equals(ProgressMonitor.Result.ERROR)) {
            throw progressMonitor.getException();
        }

        progress.setBytesWritten(size);
        broadcast(progress);

    }

}
