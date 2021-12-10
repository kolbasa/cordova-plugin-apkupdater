# Device owner app

This can be achieved with `adb`, if you have physical access to the device.

```js
await ApkUpdater.ownerInstall();
```

To use this, the app must be declared as the device owner (replace the `your.app.id` appropriately):
```
adb shell dpm set-device-owner your.app.id/de.kolbasa.apkupdater.tools.DAReceiver
```

**Beware**, after executing this command, **the app can no longer be uninstalled**.  
In this case, only the factory reset setting will help. So I wouldn't try this on your personal phone.

If you want to remove the device owner with `adb` later, you have to declare the app as `testOnly` in `AndroidManifest.xml` ([doc](https://developer.android.com/guide/topics/manifest/application-element)).
```
adb shell dpm remove-active-admin your.app.id/de.kolbasa.apkupdater.tools.DAReceiver
```

The recommended way, however, is to reset to the factory settings.