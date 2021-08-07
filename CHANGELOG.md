# CHANGELOG

## 2.4.1 - 2021-08-07
- [Fixed] Under certain circumstances, the update dialog did not open. Only with the second call of the `install()` method it showed up.
- [Fixed] `canRequestPackageInstalls()` returned an error on Android < 8.

## 2.4.0 - 2021-08-07

- [Added] Root installation can now install third-party applications.
- [Fixed] Root installation failed if the update file had spaces in the name.
- [Changed] The error handling for the download method has been improved. The thrown exception for an invalid apk/zip file is now more readable ([#28](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/28)).

## 2.3.0 - 2021-08-04

- [Added] New installation method for device owner apps ([#13](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/13)).
- [Added] Two helper methods for root installation: `isDeviceRooted()` and `requestRootAccess()`.
- [Fixed] Removed `ACCESS_NETWORK_STATE` permission.

## 2.2.0 - 2021-07-18

- [Added] TypeScript API ([#17](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/17), [#19](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/19))
- [Added] Capacitor instructions ([#25](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/25))
- [Changed] **Breaking Change:** Download config option `basicAuth.userId` was renamed to `basicAuth.user`
- [Changed] **Breaking Change:** Download result parameter `update` was renamed to `name`.

## 2.1.0 - 2021-07-17

- [Added] Support for HTTP Basic Auth added ([#23](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/23)).
- [Added] Two new functions: `canRequestPackageInstalls()`, `openInstallSetting()`
- [Fixed] The installation did not work with fullscreen apps ([#27](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/27)).
- [Changed] **Breaking Change:** Download config option `password` was renamed to `zipPassword`.

## 2.0.1 - 2021-07-13

- [Changed] Removed `line-replace` dependency in `before-plugin-add`-Hook.

## 2.0.0 - 2021-07-13

**WARNING! Breaking Changes:**
- [Changed] The complete plugin has been rewritten and greatly simplified. Details can be found in the revised `README.md`.

## 1.3.0 - 2021-07-03

- [Changed] Minimum version of `cordova-android` was raised to `9.0.0`. This allows the dependency `cordova-plugin-androidx` to be omitted  ([#22](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/1)).
- [Added] There is now an alternative installation option without AndroidX. The legacy lib `android.support.v4` is used as a replacement.
    ```
    cordova plugin add cordova-plugin-apkupdater --variable AndroidXEnabled=false
    ```

## 1.2.4 - 2021-05-30

- [Fixed] The app restart after an update was unreliable, especially with newer Android versions ([#15](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/15)).

## 1.2.3 - 2020-11-17

- [Fixed] The acceleration of the background download when switching to WiFi did not work on Android 5 & 6 ([#11](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/11)).

## 1.2.2 - 2020-09-06

- [Fixed] The `plugin.xml` file did not modify `AndroidManifest.xml` correctly. The changes could be overwritten by the `config.xml` ([#9](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/9)).
- [Fixed] The permission `android.permission.INTERNET` is now requested for older devices.
- [Fixed] Under certain circumstances not the correct error message was thrown. Instead there was only: `SyntaxError: missing ) after argument list`. The stack of the error message was not escaped correctly.

## 1.2.1 - 2020-08-05

- [Fixed] The compression node script `create-manifest.js` incorrectly searched recursively for the update file ([#8](https://github.com/kolbasa/cordova-plugin-apkupdater/issues/8#issuecomment-669294103)).

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

    ```
    // Version 1.0.0:
    cordova.plugins.apkupdater.backgroundDownload(success?: function, failure?: function, interval: number)

    // Version 1.1.0:
    cordova.plugins.apkupdater.backgroundDownload(interval: number, success?: function, failure?: function)
    ```

## 1.0.0 - 2020-06-01

- The first stable version.

