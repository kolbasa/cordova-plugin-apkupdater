var API = require('./API');

module.exports = {

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<object>|object}
     */
    getInstalledVersion: function (success, failure) {
        if (success == null && failure == null) {
            return API.getInstalledVersion();
        } else {
            API.getInstalledVersion().then(success).catch(failure);
        }
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
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<object>|object}
     */
    download: function (url, opt, success, failure) {
        if (success == null && failure == null) {
            return API.download(url, opt);
        } else {
            API.download(url, opt).then(success).catch(failure);
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
     * @returns {Promise<object>|object}
     */
    getDownloadedUpdate: function (success, failure) {
        if (success == null && failure == null) {
            return API.getDownloadedUpdate();
        } else {
            API.getDownloadedUpdate().then(success).catch(failure);
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
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<boolean>|void}
     */
    canRequestPackageInstalls: function (success, failure) {
        if (success == null && failure == null) {
            return API.canRequestPackageInstalls();
        } else {
            API.canRequestPackageInstalls().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<boolean>|void}
     */
    openInstallSetting: function (success, failure) {
        if (success == null && failure == null) {
            return API.openInstallSetting();
        } else {
            API.openInstallSetting().then(success).catch(failure);
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
     * @returns {Promise<boolean>|void}
     */
    isDeviceRooted: function (success, failure) {
        if (success == null && failure == null) {
            return API.isDeviceRooted();
        } else {
            API.isDeviceRooted().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<boolean>|void}
     */
    requestRootAccess: function (success, failure) {
        if (success == null && failure == null) {
            return API.requestRootAccess();
        } else {
            API.requestRootAccess().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    rootInstall: function (success, failure) {
        if (success == null && failure == null) {
            return API.rootInstall();
        } else {
            API.rootInstall().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<boolean>|void}
     */
    isDeviceOwner: function (success, failure) {
        if (success == null && failure == null) {
            return API.isDeviceOwner();
        } else {
            API.isDeviceOwner().then(success).catch(failure);
        }
    },

    /**
     * @param {function=} success
     * @param {function=} failure
     *
     * @returns {Promise<void>|void}
     */
    ownerInstall: function (success, failure) {
        if (success == null && failure == null) {
            return API.ownerInstall();
        } else {
            API.ownerInstall().then(success).catch(failure);
        }
    }

};
