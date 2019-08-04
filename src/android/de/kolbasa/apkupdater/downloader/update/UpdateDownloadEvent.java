package de.kolbasa.apkupdater.downloader.update;

import de.kolbasa.apkupdater.Event;

public enum UpdateDownloadEvent implements Event {

    STARTING("Download started"),
    STOPPED("Download stopped"),
    UPDATE_READY("Update ready"),
    SPEEDING_UP_DOWNLOAD("Speeding up download"),
    SLOWING_DOWN_DOWNLOAD("Slowing down download");

    private String readableString;

    @Override
    public String getMessage() {
        return this.readableString;
    }

    UpdateDownloadEvent(String readableString) {
        this.readableString = readableString;
    }
}
