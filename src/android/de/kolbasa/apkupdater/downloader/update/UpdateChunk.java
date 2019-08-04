package de.kolbasa.apkupdater.downloader.update;

import java.io.File;

public class UpdateChunk {

    private File file;

    private String checksum;

    private int progress;

    public UpdateChunk(String checksum)  {
        this.checksum = checksum;
    }

    public File getFile() {
        return file;
    }

    String getChecksum() {
        return checksum;
    }

    public int getProgress() {
        return progress;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}
