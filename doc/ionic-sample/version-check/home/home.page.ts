import {Platform} from '@ionic/angular';
import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import ApkUpdater from 'cordova-plugin-apkupdater';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})

export class HomePage {

  remote = 'https://raw.githubusercontent.com/kolbasa/cordova-plugin-apkupdater-demo/master/update';

  constructor(private httpClient: HttpClient, public platform: Platform) {
    platform.ready().then(this.update.bind(this)).catch(console.error);
  }

  async update() {
    const response = await this.httpClient.get<any>(this.remote + '/manifest.json').toPromise();

    const remoteVersion = response.version;
    const installedVersion = (await ApkUpdater.getInstalledVersion()).version.name;

    if (remoteVersion > installedVersion) {
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

}
