# WARNING: Please do not use yet, the documentation is still incomplete. I'm still working on it.

# cordova-plugin-apkupdater

This plugin gives you the tools to download an update for your android app automatically or manually without using the Play Store.
The installation file is compressed for this purpose and downloaded on the devices in small parts at a pre-set interval.

The plugin also speeds up the download when a wifi connection is detected. The goal of this plugin is to consume as little as possible of the user's mobile data quota.

## Installation

    cordova plugin add https://github.com/kolbasa/cordova-plugin-apkupdater

## Preparing your update

A nodejs script is used to prepare the update: `src/nodejs/create-manifest.js`. It compresses and splits the file into selected file sizes.
In addition, a manifest file is also created. It contains the version of the update and the checksum of all parts.

You may be wondering if compression really makes sense. I have done some tests with popular apps.
These are apk installation files that you can download freely on the Internet.


| App         | Uncompressed  | Compressed | Saving  |
|------------ |-------------: | ---------: | ------: |
| GMail       | 26.0 MB       | 14.4 MB    | 44.6%   |
| Twitter     | 31.0 MB       | 20.2 MB    | 34.8%   |
| Wikipedia   | 10.1 MB       | 6.67 MB    | 33.9%   |
| YouTube     | 37.6 MB       | 28.6 MB    | 23.9%   |
| Netflix     | 27.2 MB       | 21.9 MB    | 19.4%   |
| Whatsapp    | 24.6 MB       | 20.7 MB    | 15.8%   |
| Chrome      | 45.8 MB       | 39.8 MB    | 13.1%   |
| Google Maps | 54.4 MB       | 50.4 MB    | 7.3%    |

Honestly, one should mention that the left file sizes are not found in the App Store. 
The installation files stored there are also compressed and therefore more comparable to the right side of the table.
So we have to take care of the compression ourselves.

The script requires [7Zip](https://www.7-zip.org/) and [NodeJS](https://nodejs.org) to work (works with Linux, MacOS and Windows).
On Windows, the script looks for 7-Zip in the following folders: `%HOMEDRIVE%\7-Zip\`, `%ProgramFiles%\7-Zip\` and `%ProgramFiles(x86)%\7-Zip\`.

Usage: 

    node create.manifest.js <version> <chunk-size> <apk-path> <output-path>

* `version` - a string of your choosing, it will not be used by the plugin
* `chunk-size` - the size of one compressed chunk, defined with the units `b|k|m`, e.g. `500b`, `150k`, `1m`
* `apk-path` - the path to the apk file
* `size` - the path to which the update files are copied

Example:

    node create.manifest.js 1.0.0 100k /home/user/app.apk /home/user/update

For example, the following update files are created during execution.

    manifest.json
    1.0.0.zip.001
    1.0.0.zip.002
    1.0.0.zip.003

The contents of the manifest file will look like this:

```json
{
  "version": "1.0.0",
  "sum": "35d9fd2d688156e45b89707f650a61ac",
  "size": 5425986,
  "compressedSize": 4304842,
  "chunks": [
    "bf0b504ea0f6cdd7d3ba20d2fff48870",
    "830d523f8f2038fea5ae0fccb3dfa4c0",
    "c6a744ca828fa6dff4de888c6ec79a38"
  ]
}
```

## Updating the Android app

### `check` - check for new update

First you have to call `check`. This will download the manifest file.

```js
cordova.plugins.apkupdater.check(
    'https://your-domain.com/update',
    function (manifest) {
        // success callback
    },
    function (err) {
        // error callback
    }
);
```

The **success** function will be called with the following result:
```json
{
    "version": "1.0.0",
    "ready": false,
    "size": 4304842,
    "chunks": 3
}
```

Your application logic now has to decide what happens with this update. So your app needs to know its own version.
It can be hard coded or you can use [cordova-plugin-app-version](https://github.com/whiteoctober/cordova-plugin-app-version).
This is what the `version` field is for. It will not be parsed by the plugin, you can choose your own versioning scheme.
The `ready` field will tell you, if this update was already downloaded and is ready to install.

You can now tell the plugin to download the update. There are two functions for this: `download` and `backgroundDownload`.

### `download` - download the complete update in one go

The method `download` will download the complete update without any delays.

```js
cordova.plugins.apkupdater.download(
    function () { 
        // the update is ready to be installed
    },
    function (err) {
        // error callback
    }
);
```

### `backgroundDownload` - slowly downloads the update bit by bit at a pre-set time interval.

You can set a time interval here in which the individual parts are to be downloaded.

In my use case, I generate 90 parts, each with 50 kilobytes. Altogether approx. 4.5 MegaByte.
On a user's device, a single chunk is downloaded every 15 minutes. 

If the user uses the app all day, the update will be completely downloaded in about 22 hours.
Realistically it will take several days, because the app will be closed again and again after use.
Nevertheless it helps to keep the platform up to date.

The download will also speed up automatically if a Wifi connection is detected.

An example with a 15 minute time interval:
```js
cordova.plugins.apkupdater.backgroundDownload(
    function () {
        // the update is ready to be installed
    },
    function (err) {
        // error callback
    },
    15 * 60 * 1000 // the time interval in milliseconds.
);
```

### `stop` - stops the execution of `download` and `backgroundDownload`

This will stop the download. It will not delete the already downloaded parts. 
    The download can be continued later. For this reason, you can also view this as a pause function.

```js
cordova.plugins.apkupdater.stop(successCallback, errorCallback);
```
    
### `install` - starts the install process

As soon as the download has been completed, you can use this method to ask the user to install the apk.

```js
cordova.plugins.apkupdater.install(successCallback, errorCallback);
```

### `setObserver` - sends progress information to a function you provide

Example:
```js
cordova.plugins.apkupdater.setObserver(
    {
        downloadProgress: function (nPercentage, nBytes, nBytesWritten, nChunks, nChunksWritten) {
            console.log('Download: ' + nPercentage + ' (' + nChunksWritten + '/' + nChunks + ')');
        },
        unzipProgress: function (nPercentage) {
            console.log('Unzipping: ' + nPercentage);
        },
        event: function (sEvent) {
            console.log(sEvent);
        },
        exception: function (sMessage) {
            console.error(sMessage);
        }
    }
);
```

### `reset` - Removes all downloaded update files

This method will reset the state of the plugin. It is mostly useful only for debugging purposes.
The user himself has no access to the files and the plugin deletes old updates automatically.

```js
cordova.plugins.apkupdater.reset(successCallback, errorCallback);
```
