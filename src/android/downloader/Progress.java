package de.kolbasa.apkupdater.downloader;

import java.util.Locale;

public class Progress {

    private final long bytes;
    private long bytesWritten;
    private float percent;

    public Progress(long bytes) {
        this.bytes = bytes;
    }

    public Progress(final Progress copy) {
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
            if (this.percent > 100) {
                this.percent = 100;
            }
        }
    }

}
