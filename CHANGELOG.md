# CHANGELOG

## 2.0.0 - 2020-06-10

- [Added] Refactored Javascript API to use Promises ([#1](issues/1)).
- [Fixed] Replaced deprecated network methods in `ApkUpdater.java` ([#3](issues/3)).

**WARNING! Breaking Changes:**

The order of the arguments has been changed for the `backgroundDownload`-method.

```javascript
// Version 1.0.0:
cordova.plugins.apkupdater.backgroundDownload(success?: function, failure?: function, interval: number)

// Version 2.0.0:
cordova.plugins.apkupdater.backgroundDownload(interval: number, success?: function, failure?: function)
```



## 1.0.0 - 2020-06-01

- The first stable version.

