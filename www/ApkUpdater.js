var API = require('./API');

module.exports = {

    EVENTS: {
        STARTING: 'Download started',
        STOPPED: 'Download stopped',
        UPDATE_READY: 'Update ready',
        SPEEDING_UP_DOWNLOAD: 'Speeding up download',
        SLOWING_DOWN_DOWNLOAD: 'Slowing down download'
    },

    /**
     * @param {string} manifestUrl - The url to your hosted manifest file
     * @param {function=} success - Returns the parsed manifest object
     * @param {function=} failure
     *
     * @returns {Promise<object>|void}
     */
    check: function (manifestUrl, options, success, failure) {
        if (success == null && failure == null) {
            return API.check(manifestUrl, options);
        } else {
            API.check(manifestUrl, options).then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    download: function (success, failure) {
        if (success == null && failure == null) {
            return API.download();
        } else {
            API.download().then(success).catch(failure);
        }
    },

    /**
     * @param {number} interval - How fast individual parts should be downloaded in milliseconds.
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    backgroundDownload: function (interval, success, failure) {
        if (success == null && failure == null) {
            return API.backgroundDownload(interval);
        } else {
            API.backgroundDownload(interval).then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    stop: function (success, failure) {
        if (success == null && failure == null) {
            return API.stop();
        } else {
            API.stop().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    install: function (success, failure) {
        if (success == null && failure == null) {
            return API.install();
        } else {
            API.install().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    reset: function (success, failure) {
        if (success == null && failure == null) {
            return API.reset();
        } else {
            API.reset().then(success).catch(failure);
        }
    },

    /**
     * @param {object} observer
     * @returns {void}
     */
    setObserver: API.setObserver,

    /**
     * Gets called by the cordova plugin itself.
     * @private
     */
    _downloadProgress: API.onDownloadProgress,

    /**
     * Gets called by the cordova plugin itself.
     * @private
     */
    _unzipProgress: API.onUnzipProgress,

    /**
     * Gets called by the cordova plugin itself.
     * @private
     */
    _exception: API.onException,

    /**
     * Gets called by the cordova plugin itself.
     * @private
     */
    _event: API.onEvent

};
