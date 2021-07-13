package de.kolbasa.apkupdater.downloader;

import org.json.JSONException;
import org.json.JSONObject;

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
        bytesWritten = downloadedSize;
        if (bytes > 0) {
            percent = ((((float) downloadedSize) / bytes) * 100);
        }
        if (percent > 100) {
            percent = 100;
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("progress", ((double) (int) (percent * 100.0)) / 100.0); // Trim to 2 decimal places
        json.put("bytes", bytes);
        json.put("bytesWritten", bytesWritten);
        return json;
    }

}
