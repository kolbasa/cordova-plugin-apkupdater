package de.kolbasa.apkupdater.downloader;

import org.json.JSONException;
import org.json.JSONObject;

public class Progress {

    private final boolean round;
    private final long bytes;
    private long bytesWritten;
    private float percent;

    public Progress(long bytes) {
        this(bytes, false);
    }

    public Progress(long bytes, boolean round) {
        this.bytes = bytes;
        this.round = round;
    }

    public float getPercent() {
        return percent;
    }

    public void setBytesWritten(long bytesWritten) {
        this.bytesWritten = bytesWritten;

        if (bytes > 0) {
            percent = ((((float) this.bytesWritten) / bytes) * 100);
        }

        if (percent > 100) {
            percent = 100f;
        }

        if (round) {
            percent = Math.round(percent);
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
