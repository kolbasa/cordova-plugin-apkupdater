# Cordova Apk Updater Plugin

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/kolbasa/cordova-plugin-apkupdater/blob/master/LICENSE)

This plugin enables you to update your Android app completely without the Google Play Store.

It offers two modes for downloading the installation file.

* Download and install the complete update at once.
* Download the update slowly in the background and then ask the user to install it at a later time.


&#128073; **[DEMO APP](https://github.com/kolbasa/cordova-plugin-apkupdater-demo)** &#128072;

If you have any problems or suggestions, just [write to me](https://github.com/kolbasa/cordova-plugin-apkupdater/issues). I actively maintain the plugin and will take care of it.

## Plugin requirements

* **Android**: 5+ and `cordova-android` 9.0.0+
* **Cordova CLI**: 8.1.0+
* **7Zip** (for update compression): [Windows](doc/7zip-windows.md), [Linux](doc/7zip-linux.md), [MacOS](doc/7zip-macos.md)

## Cordova installation

    cordova plugin add cordova-plugin-apkupdater

Legacy installation without AndroidX:

    cordova plugin add cordova-plugin-apkupdater --variable AndroidXEnabled=false

## Ionic installation

For Ionic, you also need `cordova-plugin-androidx-adapter`. [Ionic Web View](https://github.com/ionic-team/cordova-plugin-ionic-webview) for Cordova requires this.

    ionic cordova plugin add cordova-plugin-apkupdater
    ionic cordova plugin add cordova-plugin-androidx-adapter

## Prepare and compress the update

To do this, use the following nodejs script: `src/nodejs/create-manifest.js`. 

It [compresses](doc/compression.md) and splits your update apk-file into small chunks. 
It also creates a manifest file. From this file the plugin gets the version and file checksums of all parts.

Usage: 

    node create-manifest.js <version> <chunk-size> <apk-path> <output-path>

| Parameter     |                                                                                             |
| ------------- | ------------------------------------------------------------------------------------------- |
| `version`     | a string of your choosing, it will not be used by the plugin                                |
| `chunk-size`  | the size of one compressed chunk, defined with the units `b,k,m`, e.g. `500b`, `150k`, `1m` |
| `apk-path`    | the path to the apk file                                                                    |
| `output-path` | the path to which the update should be copied                                               |


Example with 100 Kilobyte files:

    node create-manifest.js 1.0.0 100k /home/user/update.apk /home/user/update

This will create the following files:

    manifest.json
    update.zip.001
    update.zip.002
    update.zip.003

The contents of the manifest file will look like this:

```js
{
  "version": "1.0.0",                        // your custom version
  "sum": "35d9fd2d688156e45b89707f650a61ac", // checksum of the apk-file
  "size": 5425986,                           // size of the apk-file
  "compressedSize": 4304842,                 // size of the compressed update
  "chunks": [
    "bf0b504ea0f6cdd7d3ba20d2fff48870",      // checksum of "update.zip.001"
    "830d523f8f2038fea5ae0fccb3dfa4c0",      // checksum of "update.zip.002"
    "c6a744ca828fa6dff4de888c6ec79a38"       // checksum of "update.zip.003"
  ]
}
```

The folder is now your update, you can put it on your update server.

## Query server for update

First you have to call `check()`. This will download the manifest file.

The JavaScript API supports **promises** and **callbacks** for all methods:
```js
// promise
let manifest = await cordova.plugins.apkupdater.check('https://your-domain.com/update/manifest.json');

// alternative with callbacks
cordova.plugins.apkupdater.check('https://your-domain.com/update/manifest.json', success, failure);
```

This will return the following result:
```js
{
    "version": "1.0.0",
    "ready": false, // this update has not been downloaded yet
    "size": 4304842,
    "chunks": 3
}
```

Your application logic now has to decide what happens with this update. So your app needs to know its own version.

It can be hard coded, or you can use [cordova-plugin-app-version](https://github.com/whiteoctober/cordova-plugin-app-version).
This is what the `version` field is for. It will not be parsed by the plugin, you can choose your own versioning scheme.
 
For example, you can mark updates as optional or mandatory.

**By design, the plugin does not provide its own update dialogs.**

The `ready` field will tell you, if this update is already complete and ready to install.

## Download method 1:

The method `download` will download the complete update without any delays.

```js
// promise
await cordova.plugins.apkupdater.download();

// alternative with callbacks
cordova.plugins.apkupdater.download(success, failure);
```

## Download method 2:

The method `backgroundDownload` downloads the update slowly in the background.

You can set a time interval in which the individual parts are to be downloaded.

An example with a 15-minute time interval:
```js
// promise
await cordova.plugins.apkupdater.backgroundDownload(15 * 60 * 1000);

// alternative with callbacks
cordova.plugins.apkupdater.backgroundDownload(15 * 60 * 1000, success, failure);
```

In my use case, I generate 90 parts, each with 50 kilobytes. Altogether approx. 4.5 MegaByte.
The user's device downloads an update file every 15 minutes.

If the user does not close the app, the update will be completely downloaded in about 22 hours.
Realistically it will take several days, because the app will be closed again and again after use.
Nevertheless, it helps to keep the platform up to date.

I prioritize important updates accordingly and set the time interval lower.

As soon as the plugin downloads a part, the app knows even after a restart that it does not need to be downloaded again.

The plugin also [accelerates](https://raw.githubusercontent.com/wiki/kolbasa/cordova-plugin-apkupdater-demo/Videos/BackgroundDownload.gif) the download as soon as the connection switches to Wi-Fi.
The goal of this plugin is to consume as little as possible of the user's mobile data quota.

If you only want to use Wi-Fi for downloading, then you can simply set the interval to a very high value.

## Installation prompt

As soon as the download has been completed, you can use this method to ask the user to install the apk.

```js
// promise
await cordova.plugins.apkupdater.install();

// alternative with callbacks
cordova.plugins.apkupdater.install(success, failure);
```

## Interrupt download

This will stop both download methods. It will not delete the already downloaded parts. 

The download can be continued later. For this reason, you can also view this as a pause function.
The more update items you have generated, the less data needs to be downloaded again when you continue.

```js
// promise
await cordova.plugins.apkupdater.stop();

// alternative with callbacks
cordova.plugins.apkupdater.stop(success, failure);
```


## Monitor progress

For this purpose the plugin offers the `setObserver` method. This is optional, but can be useful if you want to offer the user a loading bar.

Example:
```js
// Works only with callbacks:
cordova.plugins.apkupdater.setObserver(
    {
        downloadProgress: function (nPercentage, nBytes, nBytesWritten, nChunks, nChunksWritten) {
            console.log('Download: ' + nPercentage + ' (' + nChunksWritten + '/' + nChunks + ')');
        },
        unzipProgress: function (nPercentage) {
            // If you have a really big application.
            console.log('Unzipping: ' + nPercentage);
        },
        event: function (sEvent) {
            // See list below
            console.log(sEvent);
        },
        exception: function (sMessage, sStack) {
            // Here the complete native error message is thrown.
            console.error(sMessage, sStack);
        }
    }
);
```

The list of all events can be found under: `cordova.plugins.apkupdater.EVENTS`:
```js
{
    STARTING: 'Download started',
    STOPPED: 'Download stopped',
    UPDATE_READY: 'Update ready',
    SPEEDING_UP_DOWNLOAD: 'Speeding up download',
    SLOWING_DOWN_DOWNLOAD: 'Slowing down download'
}
```

## Reset

The `reset` method deletes all local update files.

It is mostly useful only for debugging purposes.
The user himself has no access to the files. The plugin deletes old updates automatically.

```js
// promise
await cordova.plugins.apkupdater.reset();

// alternative with callbacks
cordova.plugins.apkupdater.reset(success, failure);
```

## Edge cases

* **"We have released a new update while a user is downloading the old one."**

    No problem. The plugin will check if the last downloaded file matches the checksum from the manifest. 

    If this check fails more than two times, the download will be stopped. Then you can `check()` again if you want to continue with the new manifest. 

    In my case I simply start the `check()` on the login page of my app. The plugin automatically deletes the old update files because they are not in the manifest.
    
## License

MIT License

Copyright (c) 2021 Michael Jedich

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
