var plugin = function () {
    return window.ApkUpdater;
};
var Events = {
    STARTING: 'Download started',
    STOPPED: 'Download stopped',
    UPDATE_READY: 'Update ready',
    SPEEDING_UP_DOWNLOAD: 'Speeding up download',
    SLOWING_DOWN_DOWNLOAD: 'Slowing down download'
};
var ApkUpdater = /** @class */ (function () {
    function ApkUpdater() {
    }
    Object.defineProperty(ApkUpdater, "EVENT_STARTING", {
        get: function () {
            return Events.STARTING;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(ApkUpdater, "EVENT_STOPPED", {
        get: function () {
            return Events.STOPPED;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(ApkUpdater, "EVENT_UPDATE_READY", {
        get: function () {
            return Events.UPDATE_READY;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(ApkUpdater, "EVENT_SPEEDING_UP_DOWNLOAD", {
        get: function () {
            return Events.SPEEDING_UP_DOWNLOAD;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(ApkUpdater, "EVENT_SLOWING_DOWN_DOWNLOAD", {
        get: function () {
            return Events.SLOWING_DOWN_DOWNLOAD;
        },
        enumerable: false,
        configurable: true
    });
    ApkUpdater.check = function () {
        var apkUpdater = plugin();
        return apkUpdater.check.apply(apkUpdater, arguments);
    };
    return ApkUpdater;
}());
export default ApkUpdater;
