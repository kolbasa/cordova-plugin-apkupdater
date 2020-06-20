# CHANGELOG

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

