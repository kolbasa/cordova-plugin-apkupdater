This is a sample implementation in a fresh `Ionic + Angular` Project:

`ionic info`:
```
Ionic:

   Ionic CLI                     : 6.17.0
   Ionic Framework               : @ionic/angular 5.6.14
   @angular-devkit/build-angular : 12.1.4
   @angular-devkit/schematics    : 12.2.2
   @angular/cli                  : 12.1.4
   @ionic/angular-toolkit        : 4.0.0

Cordova:

   Cordova CLI       : 10.0.0
   Cordova Platforms : android 9.1.0
```

`src/app/home/home.page.spec.ts`:

```ts
import {Platform} from '@ionic/angular';
import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import ApkUpdater, {Update} from 'cordova-plugin-apkupdater';

@Component({
    selector: 'app-home',
    templateUrl: 'home.page.html',
    styleUrls: ['home.page.scss'],
})

export class HomePage {

    remote = 'https://raw.githubusercontent.com/kolbasa/cordova-plugin-apkupdater-demo/master/update';

    constructor(private http: HttpClient, public platform: Platform) {
        platform.ready().then(this.update.bind(this)).catch(console.error);
    }

    async update() {
        const manifest = await this.http.get<Update>(this.remote + '/update.json').toPromise();

        const remoteVersion = manifest.app.version.code;
        const installedVersion = (await ApkUpdater.getInstalledVersion()).version.code;

        if (remoteVersion > installedVersion) {
            await ApkUpdater.download(
                this.remote + '/update.zip',
                {
                    zipPassword: 'aDzEsCceP3BPO5jy',
                    onDownloadProgress: console.log,
                    onUnzipProgress: console.log
                }
            );
            await ApkUpdater.install();
        }
    }

}
```

`src/app/app.module.ts`:
```js
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [AppComponent],
  entryComponents: [],
  imports: [BrowserModule, IonicModule.forRoot(), AppRoutingModule, HttpClientModule],
  providers: [{ provide: RouteReuseStrategy, useClass: IonicRouteStrategy }],
  bootstrap: [AppComponent],
})
export class AppModule {}
```