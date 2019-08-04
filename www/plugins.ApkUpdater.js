var exec = require('cordova/exec');

function ApkUpdater() {
    //
}

var PLUGIN = 'ApkUpdater';

var _observer;

/**
 * @param {string} manifestUrl - The url to your hosted manifest file
 * @param {function} success - Returns the parsed manifest object
 * @param {function} error
 */
ApkUpdater.prototype.check = function (manifestUrl, success, error) {
    exec(success, error, PLUGIN, 'check', [ manifestUrl ]);
};

ApkUpdater.prototype.setObserver = function (observer) {
    _observer = observer;
};

ApkUpdater.prototype.download = function (success, error) {
    exec(success, error, PLUGIN, 'download', []);
};

ApkUpdater.prototype.backgroundDownload = function (success, error, interval, wifiInterval) {
    exec(success, error, PLUGIN, 'backgroundDownload', [ interval, wifiInterval ]);
};

ApkUpdater.prototype.stop = function (success, error) {
    exec(success, error, PLUGIN, 'stop', []);
};

ApkUpdater.prototype.install = function (success, error) {
    exec(success, error, PLUGIN, 'install', []);
};

ApkUpdater.prototype.reset = function (success, error) {
    exec(success, error, PLUGIN, 'reset', []);
};

ApkUpdater.prototype._downloadProgress = function (percentage, bytes, bytesDownloaded, chunks, chunksDownloaded) {
    if (_observer != null && _observer.downloadProgress != null) {
        _observer.downloadProgress(percentage, bytes, bytesDownloaded, chunks, chunksDownloaded);
    }
};

ApkUpdater.prototype._unzipProgress = function (percentage, bytes, bytesDownloaded) {
    if (_observer != null && _observer.unzipProgress != null) {
        _observer.unzipProgress(percentage, bytes, bytesDownloaded);
    }
};

ApkUpdater.prototype._exception = function (exception, details) {
    if (_observer != null && _observer.exception != null) {
        _observer.exception(exception, details);
    }
};

ApkUpdater.prototype._event = function (event) {
    if (_observer != null && _observer.event != null) {
        _observer.event(event);
    }
};

module.exports = new ApkUpdater();
