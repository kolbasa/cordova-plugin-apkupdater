const lineReplace = require('line-replace');

module.exports = function (context) {

    let cli = context.opts.cli_variables;
    if (cli == null || cli.ANDROIDXENABLED == null || cli.ANDROIDXENABLED === 'true') {
        return; // AndroidX enabled
    }

    /**
     * @param {string} file
     * @param {number} line
     * @returns {Promise<void>}
     */
    const remove = async (file, line) => {
        await new Promise((resolve) => {
            lineReplace({
                file: file,
                line: line,
                text: '',
                addNewLine: false,
                callback: resolve
            });
        });
    };

    /**
     * @param {string} file
     * @param {number} line
     * @param {string} text
     * @returns {Promise<void>}
     */
    const replace = async (file, line, text) => {
        await new Promise((resolve) => {
            lineReplace({
                file: file,
                line: line,
                text: text,
                addNewLine: true,
                callback: resolve
            });
        });
    };

    /**
     * @param {number} length
     * @returns {string}
     */
    function indent(length) {
        return ' '.repeat(length);
    }

    (async () => {
        
        console.log('cordova-plugin-apkupdater - Installing without AndroidX.');

        const pluginXml = __dirname + '/../../../plugin.xml';
        await replace(pluginXml, 51, indent(12) + '<provider android:name="android.support.v4.content.FileProvider"');
        await remove(pluginXml, 36);
        await remove(pluginXml, 35);
        await replace(pluginXml, 34, indent(8) + '<framework src="com.android.support:appcompat-v7:$APPCOMPAT_VERSION"/>');
        await replace(pluginXml, 33, indent(8) + '<preference name="APPCOMPAT_VERSION" default="28.+" />');

        const ApkInstaller = __dirname + '/../../android/tools/ApkInstaller.java';
        await replace(ApkInstaller, 8, 'import android.support.v4.content.FileProvider;');

    })();

};