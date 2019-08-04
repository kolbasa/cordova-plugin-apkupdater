package de.kolbasa.apkupdater.downloader.progress;

public class DownloadProgress extends AbstractProgress {

    private int chunks;
    private int chunksDownloaded;

    public DownloadProgress(int bytes, int chunks) {
        super(bytes);
        this.chunks = chunks;
    }

    public DownloadProgress(final DownloadProgress copy) {
        super(copy);
        this.chunks = copy.getChunks();
        this.chunksDownloaded = copy.getChunksDownloaded();
    }

    public int getChunks() {
        return chunks;
    }

    public int getChunksDownloaded() {
        return chunksDownloaded;
    }

    public void setChunksDownloaded(int chunksDownloaded) {
        this.chunksDownloaded = chunksDownloaded;
    }
}
