# Cordova Apk Updater Plugin

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/kolbasa/cordova-plugin-apkupdater/blob/master/LICENSE)
[![Test Coverage](https://img.shields.io/badge/coverage-70%25-yellow.svg)](https://github.com/kolbasa/cordova-plugin-apkupdater/projects/5)

This plugin enables you to update your Android app completely without the Google Play Store.

It offers two modes for downloading the installation file.

* Download and install the complete update at once.
* Download the update piece by piece in the background and then ask the user to install it at a later time.


&#128073; [DEMO APP](https://github.com/kolbasa/cordova-plugin-apkupdater-demo) &#128072;

## Plugin Requirements

* **Android**: 5+
* **Cordova**: 7.1.0+
* **Cordova CLI**: 7.1.0+
* **7Zip** (for update compression): [Windows](https://www.7-zip.org/), [Linux](https://de.wikipedia.org/wiki/P7zip)

## Installation

npm:

    cordova plugin add cordova-plugin-apkupdater
    
GitHub:

    cordova plugin add https://github.com/kolbasa/cordova-plugin-apkupdater

 A [capacitor port](https://github.com/kolbasa/cordova-plugin-apkupdater/projects/6) is in the works.
 
## Prepare and compress the update

To do this, use the following nodejs script: `src/nodejs/create-manifest.js`. 

It compresses and splits the file into small chunks.
It also creates a manifest file. From this file the plugin gets the version and file checksums of all parts.

You may be wondering if compression really makes sense. I have done some tests with popular apps.

These are apk installation files that you can download freely on the Internet.


|             | Uncompressed  | Compressed | Saving  |
| ----------- |-------------: | ---------: | ------: |
| GMail       | 26.0 MB       | 14.4 MB    | 44.6%   |
| Twitter     | 31.0 MB       | 20.2 MB    | 34.8%   |
| Wikipedia   | 10.1 MB       | 6.67 MB    | 33.9%   |
| YouTube     | 37.6 MB       | 28.6 MB    | 23.9%   |
| Netflix     | 27.2 MB       | 21.9 MB    | 19.4%   |
| Whatsapp    | 24.6 MB       | 20.7 MB    | 15.8%   |


Honestly, you won't find the file sizes from the left side in the Google Play Store.
The installation files stored there are also compressed and therefore more comparable to the right side of the table.
However, we do not have this luxury and therefore have to do the compression ourselves.

The script requires [7Zip](https://www.7-zip.org/) and [NodeJS](https://nodejs.org) to work (works with Linux, MacOS and Windows).
On Windows, the script looks for 7-Zip in the following folders: `%HOMEDRIVE%\7-Zip\`, `%ProgramFiles%\7-Zip\` and `%ProgramFiles(x86)%\7-Zip\`.

Usage: 

    node create-manifest.js <version> <chunk-size> <apk-path> <output-path>

| Parameter     |                                                                                             |
| ------------- | ------------------------------------------------------------------------------------------- |
| `version`     | a string of your choosing, it will not be used by the plugin                                |
| `chunk-size`  | the size of one compressed chunk, defined with the units `b,k,m`, e.g. `500b`, `150k`, `1m` |
| `apk-path`    | the path to the apk file                                                                    |
| `output-path` | the path to which the update should be copied                                               |


Example with 100 Kilobyte files:

    node create-manifest.js 1.0.0 100k /home/user/app.apk /home/user/update

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

In my use case, I generate 90 parts, each with 50 kilobytes. Altogether approx. 4.5 MegaByte.
The user's device downloads an update file every 15 minutes.

If the user uses the app all day, the update will be completely downloaded in about 22 hours.
Realistically it will take several days, because the app will be closed again and again after use.
Nevertheless, it helps to keep the platform up to date.

As soon as the plugin downloads a part, the app knows even after a restart that it does not need to be downloaded again.

The plugin also accelerates the download as soon as the connection switches to Wi-Fi.
The goal of this plugin is to consume as little as possible of the user's mobile data quota.

An example with a 15-minute time interval:
```js
// promise
await cordova.plugins.apkupdater.backgroundDownload(15 * 60 * 1000);

// alternative with callbacks
cordova.plugins.apkupdater.backgroundDownload(15 * 60 * 1000, success, failure);
```

## Install your update

As soon as the download has been completed, you can use this method to ask the user to install the apk. That's all.

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
        exception: function (sMessage) {
            // Here the complete native error message is thrown.
            console.error(sMessage);
        }
    }
);
```

The list of all events can be found under: [`cordova.plugins.apkupdater.EVENTS`](www/ApkUpdater.js#L5-L11):
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

* "We have released a new update while a user is downloading the old update in the background."

    &#8595; &#8595; &#8595;

    No problem. The plugin will check if the last downloaded file matches the checksum from the manifest. 
    
    If this check fails more than two times, the download will be stopped. Then you can `check()` again if you want to continue with the new update. 
    
    In my case I simply start the check on the login page of my app.
    
    
