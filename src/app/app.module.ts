import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatSnackBarModule }from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { AppRoutingModule, routingComponents } from './app-routing.module';
import { AppComponent } from './app.component/app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  declarations: [AppComponent, routingComponents],
  bootstrap: [AppComponent]
})
export class AppModule { }
