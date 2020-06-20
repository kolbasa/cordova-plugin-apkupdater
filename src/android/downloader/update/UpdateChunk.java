package de.kolbasa.apkupdater.downloader.update;

import java.io.File;

public class UpdateChunk {

    private File file;

    private String checksum;

    private long bytesDownloaded;

    private boolean ready;

    public UpdateChunk(String checksum)  {
        this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public void setBytesDownloaded(long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
