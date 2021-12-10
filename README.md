Cordova Apk Updater Plugin &middot; [![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/kolbasa/cordova-plugin-apkupdater/blob/master/LICENSE) [![npm](https://img.shields.io/npm/v/cordova-plugin-apkupdater.svg)](https://www.npmjs.com/package/cordova-plugin-apkupdater) [![npm](https://img.shields.io/npm/dt/cordova-plugin-apkupdater.svg)](https://www.npmjs.com/package/cordova-plugin-apkupdater)
=========================

![Installation dialog](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Images/CordovaBot.png)

This plugin enables you to update your Android app completely without the Google Play Store.

:point_right: **[DEMO APP](https://github.com/kolbasa/cordova-plugin-apkupdater-demo)** :point_left:

If you have any problems or suggestions, just
[write to me](https://github.com/kolbasa/cordova-plugin-apkupdater/issues).  
I actively maintain the plugin.

<br>

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Plugin requirements](#plugin-requirements)
- [Installation](#installation)
    - [Cordova](#cordova)
    - [Ionic + Cordova](#ionic--cordova)
    - [Capacitor](#capacitor)
- [Basic example](#basic-example)
    - [Ionic 2+ with Typescript](#ionic-2-with-typescript)
    - [Cordova](#cordova-1)
- [API](#api)
  - [download()](#download)
  - [stop()](#stop)
  - [getInstalledVersion()](#getinstalledversion)
  - [getDownloadedUpdate()](#getdownloadedupdate)
  - [reset()](#reset)
  - [install()](#install)
    - [canRequestPackageInstalls()](#canrequestpackageinstalls)
    - [openInstallSetting()](#openinstallsetting)
  - [rootInstall()](#rootinstall)
    - [isDeviceRooted()](#isdevicerooted)
    - [requestRootAccess()](#requestrootaccess)
  - [ownerInstall()](#ownerinstall)
    - [isDeviceOwner()](#isdeviceowner)
- [Update versioning](#update-versioning)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

<br>

# Plugin requirements

* **Android**: `5+`
* **cordova**: `10.0.0+`
* **cordova-android**: `9.0.0+`

<br>

# Installation

### Cordova

    cordova plugin add cordova-plugin-apkupdater

### Ionic + Cordova

    ionic cordova plugin add cordova-androidx-build
    ionic cordova plugin add cordova-plugin-apkupdater

### Capacitor

    npm install cordova-plugin-apkupdater

# Basic example

### Ionic 2+ with Typescript

Here is the simplest example: downloading and then installing the APK:

Sample Implementation:

```ts
import ApkUpdater from 'cordova-plugin-apkupdater';

export class HomePage {

    // .
    // .
    // .

    async update() {
        await ApkUpdater.download(
            'https://your-update-server.com/update.apk',
            {
                onDownloadProgress: console.log
            }
        );
        await ApkUpdater.install();
    }

}
```

([**Complete code sample**](doc/sample/Implementation.md))

### Cordova

The same example with callbacks:

```js
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
```

<br>

# API

The JavaScript API supports **promises** and **callbacks** for all methods.  
The callback functions occupy the last two parameters.

```js
// promise
await ApkUpdater.download('https://your-update-server.com/update.apk', options);

// alternative with callbacks
ApkUpdater.download('https://your-update-server.com/update.apk', options, success, failure);
```

On this page I only show Promise examples for simplicity.

In case of a failure you get an error object with two attributes: `message` and `stack`.

This example...

```js
try {
    await ApkUpdater.download('https://wrong-url.com/update.apk');
} catch (e) {
    console.error(e.message + '\n' + e.stack);
}
```

... leads to the following output:

```
Download failed: Unable to resolve host "wrong-url.com": No address associated with hostname
de.kolbasa.apkupdater.exceptions.DownloadFailedException
  at de.kolbasa.apkupdater.downloader.FileDownloader.download(FileDownloader.java:111)
  at de.kolbasa.apkupdater.update.UpdateManager.downloadFile(UpdateManager.java:77)
  at de.kolbasa.apkupdater.update.UpdateManager.download(UpdateManager.java:133)
  at de.kolbasa.apkupdater.ApkUpdater.download(ApkUpdater.java:85)
```

<br>

## download()

```js
await ApkUpdater.download('https://your-update-server.com/update.apk', options);
```

You can also pass a `zip` file here. The zip file can even be encrypted with a password.  
However, you should make sure that the archive contains only the APK file at root level, nothing else.  
If you want to automate this, then you can also use [my script](https://github.com/kolbasa/apk-update).

Configuration (optional):

```js
const options = {
    zipPassword: 'aDzEsCceP3BPO5jy', // If an encrypted zip file is used.
    basicAuth: { // Basic access authentication
        user: 'username',
        password: 'JtE+es2GcHrjTAEU'
    },
    onDownloadProgress: function (e) {
        console.log(
            'Downloading: ' + e.progress + '%',
            '(' + e.bytesWritten + '/' + e.bytes + ')'
        );
    },
    onUnzipProgress: function (e) {
        console.log(
            'Unzipping: ' + e.progress + '%',
            '(' + e.bytesWritten + '/' + e.bytes + ')'
        );
    }
}
```

If the download is successful, you will receive detailed information about the update file.

```json
{
  "name": "update.apk",
  "path": "/data/user/0/de.kolbasa.apkupdater.demo/files/update",
  "size": 1982411,
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

<br>

## stop()

Stops the download.

```js
await ApkUpdater.stop();
```

<br>

## getInstalledVersion()

Provides detailed information about the currently installed app version.

```js
await ApkUpdater.getInstalledVersion();
```

Example output:

```js
const result = {
    "name": "Apk Updater Demo",
    "package": "de.kolbasa.apkupdater.demo",
    "firstInstallTime": 1625415754434, // Unix timestamp
    "version": {
        "code": 10000,
        "name": "1.0.0"
    }
}
```

<br>

## getDownloadedUpdate()

The downloaded update remains saved even after an app restart and can be queried as follows:

```js
await ApkUpdater.getDownloadedUpdate();
```

The result uses the same format as the output from the `download()` method.

<br>

## reset()

The `reset` method deletes all downloaded files.

It is mostly useful only for debugging purposes. The user himself has no access to the files.   
The plugin deletes old
updates automatically.

```js
await ApkUpdater.reset();
```

<br>

## install()

As soon as the download has been completed, you can use this method to ask the user to install the APK.

```js
await ApkUpdater.install();
```

When the method is invoked for the first time, the user is asked to enable a setting for installing third-party
applications
([video](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Videos/InstallSettings.gif)).

You may want to ask the user for this permission before installing the first update.  
The following two methods `canRequestPackageInstalls` and `openInstallSetting` are intended for this purpose.


### canRequestPackageInstalls()

Queries whether the installation of updates has been allowed.

```js
await ApkUpdater.canRequestPackageInstalls(); // -> true, false
```

### openInstallSetting()

Opens the settings page
([video](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Videos/OpenInstallSettings.gif)).

```js
await ApkUpdater.openInstallSetting(); // -> true, false
```

<br>

## rootInstall()

If you have a rooted device, then you can even set up unattended app update installations
([video](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Videos/RootInstall.gif)).  
If you want to test this on an emulator, I can recommend the following script: [rootAVD](https://github.com/newbit1/rootAVD)

```js
await ApkUpdater.rootInstall();
```

### isDeviceRooted()

```js
await ApkUpdater.isDeviceRooted(); // -> true, false
```

### requestRootAccess()

Requests root access
([video](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Videos/RequestRootAccess.gif)).

```js
await ApkUpdater.requestRootAccess(); // -> true, false
```

<br>

## ownerInstall()

Unattended updates can also be used by apps that are registered as device owners
([video](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Videos/OwnerInstall.gif)).  

```js
await ApkUpdater.ownerInstall();
```

Unlike root access, this can be easily set up on any Android device. Instructions can be found [here](doc/DeviceOwner.md).

### isDeviceOwner()

```js
await ApkUpdater.isDeviceOwner(); // -> true, false
```

<br>

# Update versioning

The plugin itself does not make a version comparison.  
You need to find a solution that best suits your use case.

If you always have Wi-Fi, you could download the entire apk at regular intervals and check whether the version has
changed.  
If you use mobile data, which is usually limited, then you should have a different strategy.  
You may already have a remote API and can request the latest version there.

In my case, I simply place a small `update.json` file next to the update, which stores the latest version number.  
I then simply compare this version with the internal one, which I request with the `getInstalledVersion` method.

This is also the case with the
[demo linked above](https://github.com/kolbasa/cordova-plugin-apkupdater-demo/tree/master/update).  
Here is [my script](https://github.com/kolbasa/apk-update) that I use to create the compressed update and info file.

Sample Implementation:

```ts
import ApkUpdater from 'cordova-plugin-apkupdater';

export class HomePage {

    // .
    // .
    // .

    remote = 'https://your-update-server.com'

    async update() {
        const manifest = await this.http.get<Update>(this.remote + '/update.json').toPromise();

        const remoteVersion = manifest.app.version.code;
        const installedVersion = (await ApkUpdater.getInstalledVersion()).version.code;

        if (remoteVersion > installedVersion) {
            await ApkUpdater.download(this.remote + '/update.apk');
            await ApkUpdater.install();
        }
    }
}
```

([**Complete code sample**](doc/sample/Versioning.md))

<br>

# License

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
