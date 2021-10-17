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
     * @param {string=} opt.generateChecksum
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

        opt.generateChecksum = opt.generateChecksum == null ? false: opt.generateChecksum;

        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'download', [url, basicAuth, opt.zipPassword, opt.generateChecksum]);
        });
    },

    /**
     * @returns {Promise<void>}
     */
    stop: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'stop', []);
        });
    },

    /**
     * @param {object | undefined} opt - Optional
     * @param {string=} opt.generateChecksum
     * @returns {Promise<object>}
     */
    getDownloadedUpdate: function (opt) {
        opt = opt || {};
        opt.generateChecksum = opt.generateChecksum == null ? false: opt.generateChecksum;
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'getDownloadedUpdate', [opt.generateChecksum]);
        });
    },

    /**
     * @returns {Promise<void>}
     */
    reset: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'reset', []);
        });
    },

    /**
     * @returns {Promise<boolean>}
     */
    canRequestPackageInstalls: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'canRequestPackageInstalls', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @returns {Promise<void>}
     */
    openInstallSetting: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'openInstallSetting', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @returns {Promise<boolean>}
     */
    isExternalStorageAuthorized: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'isExternalStorageAuthorized', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @returns {Promise<boolean>}
     */
    requestExternalStorageAuthorization: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'requestExternalStorageAuthorization', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @param {object | undefined} opt - Optional
     * @param {function({progress: number, bytes: number, bytesWritten: number}): void=} opt.onUnzipProgress
     * @returns {Promise<void>}
     */
    install: function (opt) {
        if (opt.onUnzipProgress != null) {
            exec(opt.onUnzipProgress, emptyFn, PLUGIN, 'addUnzipObserver');
        }
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'install', []);
        });
    },

    /**
     * @returns {Promise<boolean>}
     */
    isDeviceRooted: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'isDeviceRooted', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @returns {Promise<boolean>}
     */
    requestRootAccess: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'requestRootAccess', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @returns {Promise<void>}
     */
    rootInstall: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'rootInstall', []);
        });
    },

    /**
     * @returns {Promise<boolean>}
     */
    isDeviceOwner: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'isDeviceOwner', []);
        }).then(function (resp) {
            return resp === 1;
        });
    },

    /**
     * @returns {Promise<void>}
     */
    ownerInstall: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN, 'ownerInstall', []);
        });
    }

};

