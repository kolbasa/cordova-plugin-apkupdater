var exec = require('cordova/exec');

var PLUGIN = 'ApkUpdater';

var callbacks = {};

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

        var mappedOpt = {
            password: opt.password
        };

        if (opt.onDownloadProgress != null) {
            callbacks.downloadProgress = opt.onDownloadProgress;
            mappedOpt.addProgressObserver = true;
        }

        if (opt.onUnzipProgress != null) {
            callbacks.unzipProgress = opt.onUnzipProgress;
            mappedOpt.addUnzipObserver = true;
        }

        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'download', [url, mappedOpt]);
        }).finally(function () {
            callbacks = {};
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
    },

    /**
     * @param {string} event
     * @param {object} result
     *
     * @returns {void}
     */
    event: function (event, result) {
        if (typeof (callbacks[event]) === 'function') {
            callbacks[event](result);
        }
    }

};

