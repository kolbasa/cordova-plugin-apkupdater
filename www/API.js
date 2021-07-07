var exec = require('cordova/exec');

var MODULE_NAME = 'ApkUpdater';

var _observer;

module.exports = {

    check: function (manifestUrl, options) {
        const { basicAuth } = options || {}
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, MODULE_NAME, 'check', [manifestUrl, basicAuth]);
        });
    },

    download: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, MODULE_NAME, 'download', []);
        });
    },

    backgroundDownload: function (interval) {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, MODULE_NAME, 'backgroundDownload', [interval]);
        });
    },

    stop: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, MODULE_NAME, 'stop', []);
        });
    },

    install: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, MODULE_NAME, 'install', []);
        });
    },

    reset: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, MODULE_NAME, 'reset', []);
        });
    },

    setObserver: function (observer) {
        _observer = observer;
    },

    onDownloadProgress: function (percentage, bytes, bytesDownloaded, chunks, chunksDownloaded) {
        if (_observer != null && _observer.downloadProgress != null) {
            _observer.downloadProgress(percentage, bytes, bytesDownloaded, chunks, chunksDownloaded);
        }
    },

    onUnzipProgress: function (percentage, bytes, bytesDownloaded) {
        if (_observer != null && _observer.unzipProgress != null) {
            _observer.unzipProgress(percentage, bytes, bytesDownloaded);
        }
    },

    onException: function (exception, details) {
        if (_observer != null && _observer.exception != null) {
            _observer.exception(exception, details);
        }
    },

    onEvent: function (event) {
        if (_observer != null && _observer.event != null) {
            _observer.event(event);
        }
    }

}
