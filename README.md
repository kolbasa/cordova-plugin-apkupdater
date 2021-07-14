# Cordova Apk Updater Plugin

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/kolbasa/cordova-plugin-apkupdater/blob/master/LICENSE)

This plugin enables you to update your Android app completely without the Google Play Store.

&#128073; **[DEMO APP](https://github.com/kolbasa/cordova-plugin-apkupdater-demo)** &#128072;

If you have any problems or suggestions, just [write to me](https://github.com/kolbasa/cordova-plugin-apkupdater/issues)
.  
I actively maintain the plugin and will take care of it. Here is my
current [TODO](https://github.com/kolbasa/cordova-plugin-apkupdater/projects/9) list.

## Plugin requirements

* **Android**: 5+
* **cordova**: 10.0.0+
* **cordova-android**: 9.0.0+

## Cordova installation

    cordova plugin add cordova-plugin-apkupdater

Legacy installation without AndroidX (not recommended):

    cordova plugin add cordova-plugin-apkupdater --variable AndroidXEnabled=false

## Ionic installation

For Ionic, you also need `cordova-plugin-androidx-adapter`.  
[Ionic Web View](https://github.com/ionic-team/cordova-plugin-ionic-webview) for Cordova requires this.

    ionic cordova plugin add cordova-plugin-apkupdater
    ionic cordova plugin add cordova-plugin-androidx-adapter

## Using the plugin

### Ionic 2+ with Typescript

Here I show the simplest example: downloading and then installing the APK:

```ts
import {Platform} from '@ionic/angular';
import {Component} from '@angular/core';

@Component({
    selector: 'app-home',
    templateUrl: 'home.page.html'
})

export class HomePage {

    constructor(public platform: Platform) {
        platform.ready().then(this.update.bind(this)).catch(console.error);
    }

    async update() {
        const apkUpdater = (window as any).ApkUpdater;
        await apkUpdater.download('https://your-update-server.com/update.apk', {onDownloadProgress: console.log});
        await apkUpdater.install();
    }

}
```

### Cordova / Ionic 1

The same example with callbacks:

```js
function onDeviceReady() {
    ApkUpdater.download(
        'https://your-update-server.com/update.apk',
        {
            onDownloadProgress: console.log
        },
        function () {
            ApkUpdater.install(console.log, console.error);
        },
        console.error
    );
}

document.addEventListener("deviceready", onDeviceReady, false);
```

## Download update

The JavaScript API supports **promises** and **callbacks** for all methods:

```js
// promise
await ApkUpdater.download('https://your-update-server.com/update.apk', options);

// alternative with callbacks
ApkUpdater.download('https://your-update-server.com/update.apk', options, success, failure);
```

You can also pass a zip file here.  
However, you should make sure that the archive contains only the APK file, nothing else.  
Under `src/nodejs/compress-apk.js` you will find the script I use for automated compression.

The download method accepts the following options:

```js
let options = {
    password: 'aDzEsCceP3BPO5jy', // If an encrypted zip file is used.
    onDownloadProgress: function (e) {
        console.log('Downloading: ' + e.progress + '% (' + e.bytesWritten + '/' + e.bytes + ')');
    },
    onUnzipProgress: function (e) {
        console.log('Unzipping: ' + e.progress + '% (' + e.bytesWritten + '/' + e.bytes + ')');
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
    "checksum": "d90916f513b1226e246ecb4d64acffae", // MD5
    "app": {
        "name": "Apk Updater Demo",
        "package": "de.kolbasa.apkupdater.demo",
        "version": {
            "code": 10001,
            "name": "1.0.1"
        }
    }
}
```

In case of a failure you get an error object with two attributes: `message` and `stack`.

This example...

```js
try {
    await ApkUpdater.download('https://your-server.com/update.zip', {password: 'wrongPassword'});
} catch (e) {
    console.error(e.message + '\n' + e.stack);
}
```

... leads to the following output:

```
Download failed
net.lingala.zip4j.exception.ZipException: Wrong password!
  at net.lingala.zip4j.crypto.StandardDecrypter.init(StandardDecrypter.java:61)
  at net.lingala.zip4j.crypto.StandardDecrypter.<init>(StandardDecrypter.java:31)
  at net.lingala.zip4j.io.inputstream.ZipStandardCipherInputStream.initializeDecrypter(ZipStandardCipherInputStream.java:20)
  at net.lingala.zip4j.io.inputstream.ZipStandardCipherInputStream.initializeDecrypter(ZipStandardCipherInputStream.java:10)
  at net.lingala.zip4j.io.inputstream.CipherInputStream.<init>(CipherInputStream.java:25)
  at net.lingala.zip4j.io.inputstream.ZipStandardCipherInputStream.<init>(ZipStandardCipherInputStream.java:14)
  at net.lingala.zip4j.io.inputstream.ZipInputStream.initializeCipherInputStream(ZipInputStream.java:236)
  at net.lingala.zip4j.io.inputstream.ZipInputStream.initializeEntryInputStream(ZipInputStream.java:223)
  at net.lingala.zip4j.io.inputstream.ZipInputStream.getNextEntry(ZipInputStream.java:113)
  at net.lingala.zip4j.io.inputstream.ZipInputStream.getNextEntry(ZipInputStream.java:83)
  at de.kolbasa.apkupdater.tools.Unzipper.unzip(Unzipper.java:51)
  at de.kolbasa.apkupdater.update.UpdateManager.unzipFile(UpdateManager.java:90)
  at de.kolbasa.apkupdater.update.UpdateManager.download(UpdateManager.java:121)
  at de.kolbasa.apkupdater.ApkUpdater.download(ApkUpdater.java:90)
  at de.kolbasa.apkupdater.ApkUpdater.lambda$execute$3$ApkUpdater(ApkUpdater.java:191)
  at de.kolbasa.apkupdater.-$$Lambda$ApkUpdater$i2uPxQeilYT0voSmjrvq6lzNQe0.run(Unknown Source:6)
  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
  at java.lang.Thread.run(Thread.java:920)
```

This also applies to all the remaining methods listed below.

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

The `reset` method deletes all downloaded files.

It is mostly useful only for debugging purposes. The user himself has no access to the files. The plugin deletes old
updates automatically.

```js
// promise
await ApkUpdater.reset();

// alternative with callbacks
ApkUpdater.reset(success, failure);
```

## Version checks

The plugin itself does not make a version comparison.  
You have to build the logic yourself how the app can determine if an update is needed.

In my case, I simply place a `manifest.json` file next to the update, which stores the latest version number.  
I then simply compare this version with the internal one, which I request with the `getInstalledVersion` method.

This is also the case with
the [demo linked above](https://github.com/kolbasa/cordova-plugin-apkupdater-demo/tree/master/update).

Here is a simple example:

```js
const REMOTE = 'https://raw.githubusercontent.com/kolbasa/cordova-plugin-apkupdater-demo/master/update';

let response = await new Promise(function (resolve, reject) {
    cordova.plugin.http.sendRequest(REMOTE + '/manifest.json', {responseType: 'json', method: 'get'}, resolve, reject);
});

let remoteVersion = response.data.version;
let installedVersion = (await ApkUpdater.getInstalledVersion()).version.name;

if (remoteVersion > installedVersion) {
    await ApkUpdater.download(REMOTE + '/update.zip', {password: 'aDzEsCceP3BPO5jy', onDownloadProgress: console.log});
    await ApkUpdater.install();
}
```

Ionic 2+ with Typescript:

```ts
import {Platform} from '@ionic/angular';
import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
    selector: 'app-home',
    templateUrl: 'home.page.html'
})

export class HomePage {

    remote = 'https://raw.githubusercontent.com/kolbasa/cordova-plugin-apkupdater-demo/master/update';

    constructor(private httpClient: HttpClient, public platform: Platform) {
        platform.ready().then(this.update.bind(this)).catch(console.error);
    }

    async update() {
        const apkUpdater = (window as any).ApkUpdater;

        const response = await this.httpClient.get<any>(this.remote + '/manifest.json').toPromise();

        const remoteVersion = response.version;
        const installedVersion = (await apkUpdater.getInstalledVersion()).version.name;

        if (remoteVersion > installedVersion) {
            await apkUpdater.download(this.remote + '/update.zip', {
                password: 'aDzEsCceP3BPO5jy',
                onDownloadProgress: console.log
            });
            await apkUpdater.install();
        }
    }

}
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
