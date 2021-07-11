# Cordova Apk Updater Plugin

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/kolbasa/cordova-plugin-apkupdater/blob/master/LICENSE)

This plugin enables you to update your Android app completely without the Google Play Store.

&#128073; **[DEMO APP](https://github.com/kolbasa/cordova-plugin-apkupdater-demo)** &#128072;

If you have any problems or suggestions, just [write to me](https://github.com/kolbasa/cordova-plugin-apkupdater/issues)
. I actively maintain the plugin and will take care of it.

## Plugin requirements

* **Android**: 5+ and `cordova-android` 9.0.0+
* **Cordova CLI**: 8.1.0+

## Cordova installation

    cordova plugin add cordova-plugin-apkupdater

Legacy installation without AndroidX:

    cordova plugin add cordova-plugin-apkupdater --variable AndroidXEnabled=false

## Ionic installation

For Ionic, you also need `cordova-plugin-androidx-adapter`
. [Ionic Web View](https://github.com/ionic-team/cordova-plugin-ionic-webview) for Cordova requires this.

    ionic cordova plugin add cordova-plugin-apkupdater
    ionic cordova plugin add cordova-plugin-androidx-adapter


## Download update

The JavaScript API supports **promises** and **callbacks** for all methods:

```js
// promise
let manifest = await ApkUpdater.download('https://your-domain.com/update/update.apk', options);

// alternative with callbacks
ApkUpdater.download('https://your-domain.com/update/update.apk', options, success, failure);
```

You can also pass a zip file here. 
However, you should make sure that the archive contains only the APK file, nothing else.

The download method accepts the following options:
```js
let options = {
  password: 'aDzEsCceP3BPO5jy', // If the zip file is encrypted.
  onDownloadProgress: function (e) {
    console.log('Downloading: ' + e.progress + '%');
  },
  onUnzipProgress: function (e) {
    console.log('Unzipping: ' + e.progress + '%');
  }
}
```

If the download is successful, you will receive detailed information about the update file.
```js
let response = {
  "zip": "update-encrypted.zip",
  "update": "update.apk",
  "path": "/data/user/0/de.kolbasa.apkupdater.demo/files/update",
  "size": 1982411,
  "checksum": "d90916f513b1226e246ecb4d64acffae",
  "app": {
    "name": "Apk Updater Demo",
    "package": "de.kolbasa.apkupdater.demo",
    "version": {
      "code": 10000,
      "name": "1.0.0"
    }
  }
}
```

## Check installed version

You can also get detailed information about the currently used version.

```js
// promise
await ApkUpdater.getInstalledVersion();

// alternative with callbacks
ApkUpdater.getInstalledVersion(success, failure);
```

Example output:
```js
let response = {
  "name": "Apk Updater Demo",
  "package": "de.kolbasa.apkupdater.demo",
  "firstInstallTime": 1625415754434,
  "version": {
    "code": 10000,
    "name": "1.0.0"
  }
}
```

## Check cached update version

The downloaded update remains saved even after an app restart and can be queried as follows:

```js
// promise
await ApkUpdater.getDownloadedUpdate();

// alternative with callbacks
ApkUpdater.getDownloadedUpdate(success, failure);
```

## Installation prompt

As soon as the download has been completed, you can use this method to ask the user to install the apk.

```js
// promise
await ApkUpdater.install();

// alternative with callbacks
ApkUpdater.install(success, failure);
```

If you have a rooted device, then you do not need to ask the user for permission to install the update.

```js
// promise
await ApkUpdater.rootInstall();

// alternative with callbacks
ApkUpdater.rootInstall(success, failure);
```

## Interrupt download

```js
// promise
await ApkUpdater.stop();

// alternative with callbacks
ApkUpdater.stop(success, failure);
```


## Reset

The `reset` method deletes all local update files.

It is mostly useful only for debugging purposes. The user himself has no access to the files. The plugin deletes old
updates automatically.

```js
// promise
await ApkUpdater.reset();

// alternative with callbacks
ApkUpdater.reset(success, failure);
```

## License

MIT License

Copyright (c) 2021 Michael Jedich

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
