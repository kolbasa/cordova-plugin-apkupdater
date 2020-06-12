# CHANGELOG

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

