package de.kolbasa.apkupdater.downloader.progress;

import java.util.Locale;

public abstract class AbstractProgress {

    private long bytes;
    private long bytesWritten;
    private float percent;

    AbstractProgress(long bytes) {
        this.bytes = bytes;
    }

    AbstractProgress(final AbstractProgress copy) {
        this.bytes = copy.getBytes();
        this.bytesWritten = copy.getBytesWritten();
        this.percent = copy.getPercent();
    }

    public long getBytes() {
        return bytes;
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

    public float getPercent() {
        return percent;
    }

    public void setBytesWritten(long downloadedSize) {
        this.bytesWritten = downloadedSize;
        if (bytes > 0) {
            float percent = ((((float) downloadedSize) / bytes) * 100);
            this.percent = Float.parseFloat(String.format(Locale.ENGLISH, "%.2f", percent));
        }
    }

}
