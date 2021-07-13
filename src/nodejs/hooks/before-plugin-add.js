module.exports = function (context) {

    let cli = context.opts.cli_variables;
    if (cli == null || cli.ANDROIDXENABLED == null || cli.ANDROIDXENABLED === 'true') {
        return; // AndroidX enabled
    }

    let fs = require('fs');
    let UTF8 = 'utf8';

    const pluginXml = __dirname + '/../../../plugin.xml';
    let data = fs.readFileSync(pluginXml, UTF8);
    data = data.replace(/<provider android:name="androidx.core.content.FileProvider"/g, '<provider android:name="android.support.v4.content.FileProvider"');
    data = data.replace(/<preference name="AndroidXEnabled" value="true" \/>/g, '');
    data = data.replace(/<framework src="androidx.core:core:1.6.0" \/>/g, '<preference name="APPCOMPAT_VERSION" default="28.+"/><framework src="com.android.support:appcompat-v7:$APPCOMPAT_VERSION"/>');
    fs.writeFileSync(pluginXml, data, UTF8);

    const ApkInstaller = __dirname + '/../../android/tools/ApkInstaller.java';
    data = fs.readFileSync(ApkInstaller, UTF8);
    data = data.replace(/import androidx.core.content.FileProvider;/g, 'import android.support.v4.content.FileProvider;');
    fs.writeFileSync(ApkInstaller, data, UTF8);

};