package de.kolbasa.apkupdater.downloader.progress;

public class UnzipProgress extends AbstractProgress {

    public UnzipProgress(int bytes) {
        super(bytes);
    }

    public UnzipProgress(final UnzipProgress copy) {
        super(copy);
    }

}
