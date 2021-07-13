package de.kolbasa.apkupdater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

import de.kolbasa.apkupdater.downloader.Progress;
import de.kolbasa.apkupdater.update.Update;
import de.kolbasa.apkupdater.update.UpdateManager;

class ApkUpdater {

    private static final String TEMP_DIR = "apk-updater";
    private static final String APK = "update.apk";
    private static final String ARCHIVE = "update.zip";
    private static final String ARCHIVE_ENCRYPTED = "update-encrypted.zip";
    private static final String ARCHIVE_PASSWORD = "aDzEsCceP3BPO5jy";
    private static final String FILE_SERVER = "http://localhost:3101/update";

    private static File getDownloadDirectory() {
        String path = null;
        try {
            path = Files.createTempDirectory(TEMP_DIR + "." + UUID.randomUUID().toString()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (path == null) {
            throw new NullPointerException("Can not create download directory");
        }
        return new File(path);
    }

    @Nested
    @DisplayName("Download update")
    class Download {

        @Test
        @DisplayName("Download apk")
        void downloadApk() throws Exception {
            File downloadDir = getDownloadDirectory();
            UpdateManager manager = new UpdateManager(downloadDir);
            Update update = manager.download(FILE_SERVER + "/" + APK);

            assertNotNull(update);
            assertNotNull(update.getApk());
            assertTrue(update.getApk().exists());
            assertTrue(new File(downloadDir, APK).exists());
            assertNotNull(update.getChecksum());

            assertNull(update.getZip());
            assertFalse(new File(downloadDir, ARCHIVE).exists());
        }

        @Test
        @DisplayName("Download archive")
        void downloadArchive() throws Exception {
            File downloadDir = getDownloadDirectory();
            UpdateManager manager = new UpdateManager(downloadDir);
            Update update = manager.download(FILE_SERVER + "/" + ARCHIVE);

            assertNotNull(update);
            assertNotNull(update.getApk());
            assertTrue(update.getApk().exists());
            assertTrue(update.getApk().length() > 0);
            assertTrue(new File(downloadDir, APK).exists());
            assertNotNull(update.getChecksum());

            assertNotNull(update.getZip());
            assertFalse(update.getZip().exists());
            assertFalse(new File(downloadDir, ARCHIVE).exists());
        }

        @Test
        @DisplayName("Download encrypted archive")
        void downloadEncryptedArchive() throws Exception {
            File downloadDir = getDownloadDirectory();
            UpdateManager manager = new UpdateManager(downloadDir);
            Update update = manager.download(FILE_SERVER + "/" + ARCHIVE_ENCRYPTED, ARCHIVE_PASSWORD);

            assertNotNull(update);
            assertNotNull(update.getApk());
            assertTrue(update.getApk().exists());
            assertTrue(update.getApk().length() > 0);
            assertTrue(new File(downloadDir, APK).exists());
            assertNotNull(update.getChecksum());

            assertNotNull(update.getZip());
            assertFalse(update.getZip().exists());
            assertFalse(new File(downloadDir, ARCHIVE_ENCRYPTED).exists());
        }

        @Nested
        @DisplayName("Monitor progress")
        class Monitor {

            @Test
            @DisplayName("Download")
            void download() throws Exception {
                File downloadDir = getDownloadDirectory();
                UpdateManager manager = new UpdateManager(downloadDir);
                final ArrayList<Progress> events = new ArrayList<>();
                manager.addDownloadObserver((o, arg) -> {
                    if (arg instanceof Progress) {
                        events.add(new Progress((Progress) arg));
                    }
                });

                manager.download(FILE_SERVER + "/" + APK);
                assertEquals(3, events.size());
                assertEquals(0, events.get(0).getPercent());
                assertEquals(0, events.get(0).getBytesWritten());
                assertTrue(events.get(1).getBytesWritten() > 0);
                assertEquals(100, events.get(2).getPercent());
            }

            @Test
            @DisplayName("Unzip")
            void unzip() throws Exception {
                File downloadDir = getDownloadDirectory();
                UpdateManager manager = new UpdateManager(downloadDir);
                final ArrayList<Progress> events = new ArrayList<>();
                manager.addUnzipObserver((o, arg) -> {
                    if (arg instanceof Progress) {
                        events.add(new Progress((Progress) arg));
                    }
                });

                manager.download(FILE_SERVER + "/" + ARCHIVE);
                assertEquals(3, events.size());
                assertEquals(0, events.get(0).getPercent());
                assertEquals(0, events.get(0).getBytesWritten());
                assertTrue(events.get(1).getBytesWritten() > 0);
                assertEquals(100, events.get(2).getPercent());
            }

        }

    }

}
