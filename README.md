# cordova-plugin-apkupdater

This plugin gives you the tools to download an update for your android app automatically or manually without using the Play Store.
The installation file is compressed for this purpose and downloaded on the devices in small parts at a pre-set interval.

The plugin also speeds up the download when a wifi connection is detected. The goal of this plugin is to consume as little as possible of the user's mobile data quota.

## Installation

    cordova plugin add https://github.com/kolbasa/cordova-plugin-apkupdater

## Preparing your update

A nodejs script is used to prepare the update: `src/nodejs/create-manifest.js`. It compresses and splits the file into selected file sizes.
In addition, a manifest file is also created. It contains the version of the update and the checksum of all parts.

```json
{
  "version": "1.0.0",
  "sum": "35d9fd2d688156e45b89707f650a61ac",
  "size": 600,
  "compressedSize": 351,
  "chunks": [
    "bf0b504ea0f6cdd7d3ba20d2fff48870",
    "830d523f8f2038fea5ae0fccb3dfa4c0",
    "c6a744ca828fa6dff4de888c6ec79a38"
  ]
}
```

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

Honestly, one should mention that the left file sizes are not found in the App Store. Since these installation files are also compressed and rather to compare with the right side.

The script requires [7Zip](https://www.7-zip.org/) and [NodeJS](https://nodejs.org) to work.

Usage: 
```
node create.manifest.js <version> <chunk-size> <apk-path> <output-path>
```
sample:
```bash
node create.manifest.js 1.0.0 100k /home/user/app.apk /home/user/update
```
