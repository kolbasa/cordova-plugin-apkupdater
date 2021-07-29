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
     * @param {string=} opt.zipPassword
     * @param {object=} opt.basicAuth
     * @param {string=} opt.basicAuth.user
     * @param {string=} opt.basicAuth.password
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

        var basicAuth;
        if (opt.basicAuth != null && opt.basicAuth.user != null && opt.basicAuth.password != null) {
            basicAuth = opt.basicAuth.user + ':' + opt.basicAuth.password;
        }

        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'download', [url, basicAuth, opt.zipPassword]);
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
    ownerInstall: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'ownerInstall', []);
        });
    },

    /**
     * @returns {Promise}
     */
    canRequestPackageInstalls: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'canRequestPackageInstalls', []);
        }).then(function (resp) {
            return resp === 'true';
        });
    },

    /**
     * @returns {Promise}
     */
    openInstallSetting: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'openInstallSetting', []);
        });
    },

    /**
     * @returns {Promise}
     */
    isDeviceOwner: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'isDeviceOwner', []);
        }).then(function (resp) {
            return resp === 'true';
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

