package de.kolbasa.apkupdater.downloader.manifest;

import java.io.File;
import java.util.List;

import de.kolbasa.apkupdater.downloader.update.UpdateChunk;

public class Manifest {

    private String version;
    private String checksum;
    private File file;
    private File updateFile;

    private Integer size;
    private Integer compressedSize;

    private List<UpdateChunk> chunks;

    Manifest(String version, String checksum, List<UpdateChunk> chunks,
             Integer size, Integer compressedSize) {
        this.version = version;
        this.checksum = checksum;
        this.chunks = chunks;
        this.size = size;
        this.compressedSize = compressedSize;
    }

    public String getVersion() {
        return this.version;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public int getSize() {
        return this.size;
    }

    public List<UpdateChunk> getChunks() {
        return this.chunks;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Integer getCompressedSize() {
        return compressedSize;
    }

    public File getUpdateFile() {
        return updateFile;
    }

    public void setUpdateFile(File updateFile) {
        this.updateFile = updateFile;
    }
}
