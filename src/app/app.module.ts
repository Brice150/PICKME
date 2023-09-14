import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { appRouter } from './app.router';
import { SelectModule } from './select/select.module';
import { ProfileModule } from './profile/profile.module';
import { MoreinfoModule } from './moreinfo/moreinfo.module';
import { MatchModule } from './match/match.module';
import { LikeModule } from './like/like.module';
import { ConnectModule } from './connect/connect.module';
import { AdminModule } from './admin/admin.module';
import { ToastrModule } from 'ngx-toastr';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

@NgModule({
  imports: [
    BrowserModule,
    appRouter,
    SelectModule,
    ProfileModule,
    MoreinfoModule,
    MatchModule,
    LikeModule,
    ConnectModule,
    AdminModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    HttpClientModule,
    MatSnackBarModule,
    MatDialogModule,
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent],
  providers: [{ provide: LocationStrategy, useClass: HashLocationStrategy }],
})
export class AppModule {}
