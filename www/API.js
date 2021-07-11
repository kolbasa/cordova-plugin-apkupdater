var exec = require('cordova/exec');

var PLUGIN = 'ApkUpdater';

var callbacks = {};

/**
 * @param {string} str
 * @param {string} replace
 * @param {string} replaceWith
 * @returns {string}
 */
function replaceAll(str, replace, replaceWith) {
    if (str == null) {
        return str;
    }
    if (typeof (String.prototype.replaceAll) === 'function') {
        return str.replaceAll(replace, replaceWith);
    }
    var esc = ['-', '[', ']', '/', '{', '}', '(', ')', '*', '+', '?', '.', '\\', '^', '$', '|'].join('\\');
    replace = replace.replace(new RegExp('[' + esc + ']', 'g'), '\\$&');
    return str.replace(new RegExp(replace, 'g'), replaceWith);
}

/**
 * @param {string} str
 * @returns {string}
 */
function unescapeEcmascript(str) {
    str = replaceAll(str, '\\"', '"');
    str = replaceAll(str, '\\/', '/');
    str = replaceAll(str, '\\\'', '\'');
    str = replaceAll(str, '\\t', '  ');
    str = replaceAll(str, '\\n', '\n');
    return str;
}

/**
 * @param {function} reject
 * @returns {(function(*=): void)|*}
 */
function unescapeError(reject) {
    return function (err) {
        if (err != null) {
            if (err.message != null) {
                err.message = unescapeEcmascript(err.message);
            }
            if (err.stack != null) {
                err.stack = unescapeEcmascript(err.stack);
            }
        }
        reject(err);
    };
}

module.exports = {

    /**
     * @returns {Promise<object>}
     */
    getInstalledVersion: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, unescapeError(reject), PLUGIN, 'getInstalledVersion', []);
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
            exec(resolve, unescapeError(reject), PLUGIN, 'download', [url, mappedOpt]);
        }).finally(function () {
            callbacks = {};
        });
    },

    /**
     * @returns {Promise}
     */
    stop: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, unescapeError(reject), PLUGIN, 'stop', []);
        });
    },

    /**
     * @returns {Promise<object>}
     */
    getDownloadedUpdate: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, unescapeError(reject), PLUGIN, 'getDownloadedUpdate', []);
        });
    },

    /**
     * @returns {Promise}
     */
    install: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, unescapeError(reject), PLUGIN, 'install', []);
        });
    },

    /**
     * @returns {Promise}
     */
    rootInstall: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, unescapeError(reject), PLUGIN, 'rootInstall', []);
        });
    },

    /**
     * @returns {Promise}
     */
    reset: function () {
        return new Promise(function (resolve, reject) {
            exec(resolve, unescapeError(reject), PLUGIN, 'reset', []);
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

