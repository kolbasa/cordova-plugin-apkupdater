var exec = require('cordova/exec');

var PLUGIN = 'ApkUpdater';

function emptyFn() {
    //
}

module.exports = {

    /**
     * @returns {Promise<object>}
     */
    getInstalledVersion: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'getInstalledVersion', []);
        });
    },

    /**
     * @param {string} url - Your apk or zip-archive
     * @param {object | undefined} opt - Optional
     * @param {string=} opt.password
     * @param {function({progress: number, bytes: number, bytesWritten: number}): void=} opt.onDownloadProgress
     * @param {function({progress: number, bytes: number, bytesWritten: number}): void=} opt.onUnzipProgress
     * @returns {Promise<object>}
     */
    download: function (url, opt) {
        opt = opt || {};

        if (opt.onDownloadProgress != null) {
            exec(opt.onDownloadProgress, emptyFn, PLUGIN, 'addProgressObserver');
        }

        if (opt.onUnzipProgress != null) {
            exec(opt.onUnzipProgress, emptyFn, PLUGIN, 'addUnzipObserver');
        }

        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'download', [url, opt.password]);
        });
    },

    /**
     * @returns {Promise}
     */
    stop: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'stop', []);
        });
    },

    /**
     * @returns {Promise<object>}
     */
    getDownloadedUpdate: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'getDownloadedUpdate', []);
        });
    },

    /**
     * @returns {Promise}
     */
    install: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'install', []);
        });
    },

    /**
     * @returns {Promise}
     */
    rootInstall: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'rootInstall', []);
        });
    },

    /**
     * @returns {Promise}
     */
    reset: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'reset', []);
        });
    }

};

