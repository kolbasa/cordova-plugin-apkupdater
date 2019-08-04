package de.kolbasa.apkupdater.downloader.manifest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;

import de.kolbasa.apkupdater.downloader.FileDownloader;
import de.kolbasa.apkupdater.downloader.update.UpdateChunk;

public class ManifestDownloader extends FileDownloader {

    private static final String MANIFEST_FILE = "manifest.json";
    private static final String VERSION_KEY = "version";
    private static final String CHUNKS_KEY = "chunks";
    private static final String SIZE_KEY = "size";
    private static final String COMPRESSED_SIZE_KEY = "compressedSize";
    private static final String SUM_KEY = "sum";

    private String serverURL;
    private String downloadPath;
    private int timeout;

    public ManifestDownloader(String manifestUrl, String downloadPath, int timeout) {
        this.serverURL = manifestUrl;
        this.downloadPath = downloadPath;
        this.timeout = timeout;
    }

    @Override
    public void onProgress(int total, int current) {
        //
    }

    public void stop() {
        this.interrupt();
    }

    private Manifest parse(JSONObject manifest) {

        String version = (String) manifest.get(VERSION_KEY);

        if (version == null) {
            throw new NullPointerException("Version property missing!");
        }

        Long size = (Long) manifest.get(SIZE_KEY);

        if (size == null) {
            throw new NullPointerException("Size property missing!");
        }

        Long compressedSize = (Long) manifest.get(COMPRESSED_SIZE_KEY);

        if (compressedSize == null) {
            throw new NullPointerException("Compressed size property missing!");
        }

        String sum = (String) manifest.get(SUM_KEY);

        if (sum == null) {
            throw new NullPointerException("Checksum ('sum') property missing!");
        }

        JSONArray checksums = (JSONArray) manifest.get(CHUNKS_KEY);
        if (checksums == null) {
            throw new NullPointerException("Chunks property missing!");
        }

        ArrayList<UpdateChunk> chunks = new ArrayList<>();
        for (Object checksum : checksums) {
            chunks.add(new UpdateChunk((String) checksum));
        }

        return new Manifest(version, sum, chunks, size.intValue(), compressedSize.intValue());

    }

    public Manifest download() throws IOException, ParseException {
        super.download(this.serverURL + "/" + MANIFEST_FILE, this.downloadPath, this.timeout);
        File manifestFile = new File(this.downloadPath, MANIFEST_FILE);
        FileReader fileReader = new FileReader(manifestFile);
        JSONObject m = (JSONObject) new JSONParser().parse(fileReader);
        Manifest manifest = parse(m);
        manifest.setFile(manifestFile);
        fileReader.close();
        return manifest;
    }

}
