# CHANGELOG

## 1.2.3 - 2020-11-17

* [Fixed] The acceleration of the background download when switching to WiFi did not work on Android 5 & 6 ([#11](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/11)).

## 1.2.2 - 2020-09-06

* [Fixed] The `plugin.xml` file did not modify `AndroidManifest.xml` correctly. The changes could be overwritten by the `config.xml` ([#9](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/9)).
* [Fixed] The permission `android.permission.INTERNET` is now requested for older devices.
* [Fixed] Under certain circumstances not the correct error message was thrown. Instead there was only: `SyntaxError: missing ) after argument list`. The stack of the error message was not escaped correctly.

## 1.2.1 - 2020-08-05

* [Fixed] The compression node script `create-manifest.js` incorrectly searched recursively for the update file ([#8](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/8#issuecomment-669294103)).

## 1.2.0 - 2020-07-12

- [Changed] Migrated to the new Android Support Library: AndroidX ([#2](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/2#issuecomment-656645632)).

    **WARNING! Breaking Changes:**

    The minimum Cordova CLI version is increased from `7.1.0` to `8.1.0`.

## 1.1.5 - 2020-06-21

- [Fixed] In random cases, the method `backgroundDownload` could cause an infinite loop if you lost the internet connection.

## 1.1.4 - 2020-06-21

- [Fixed] The `reset` method now works even if no manifest has been downloaded before. You can now also run it when a download is in progress.

## 1.1.3 - 2020-06-20

- [Changed] `README.md` improvements

## 1.1.2 - 2020-06-20

- [Added] `check` method now also accepts a direct URL to the `manifest.json` file ([#4](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/4)).
- [Changed] `setObserver` now provides significantly more reliable progress information. Especially if you pause the download and start it again.
- [Fixed] Fixed `java.lang.IllegalArgumentException: NetworkCallback was already unregistered`. The error occurred during a cleanup procedure for the method `backgroundDownload`.

## 1.1.1 - 2020-06-11

- [Added] published to npm registry

## 1.1.0 - 2020-06-11

- [Added] Refactored Javascript API to use Promises ([#1](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/1)).
- [Fixed] Replaced deprecated network methods in `ApkUpdater.java` ([#3](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/3)).

    **WARNING! Breaking Changes:**

    The order of the arguments has been changed for the `backgroundDownload`-method.

    ```javascript
    // Version 1.0.0:
    cordova.plugins.apkupdater.backgroundDownload(success?: function, failure?: function, interval: number)

    // Version 1.1.0:
    cordova.plugins.apkupdater.backgroundDownload(interval: number, success?: function, failure?: function)
    ```

## 1.0.0 - 2020-06-01

- The first stable version.

