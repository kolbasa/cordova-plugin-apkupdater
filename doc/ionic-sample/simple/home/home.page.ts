import {Platform} from '@ionic/angular';
import {Component} from '@angular/core';

import ApkUpdater from 'cordova-plugin-apkupdater';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})

export class HomePage {

  remote = 'https://raw.githubusercontent.com/kolbasa/cordova-plugin-apkupdater-demo/master/update';

  constructor(public platform: Platform) {
    platform.ready().then(this.update.bind(this)).catch(console.error);
  }

  async update() {
    await ApkUpdater.download(
        this.remote + '/update.zip',
        {
          zipPassword: 'aDzEsCceP3BPO5jy',
          onDownloadProgress: progress => console.log(progress),
          onUnzipProgress: progress => console.log(progress)
        }
    );
    await ApkUpdater.install();
  }

}
