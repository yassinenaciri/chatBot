import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChatModule } from '@progress/kendo-angular-conversational-ui';

@NgModule({
  imports: [BrowserModule, BrowserAnimationsModule, SharedModule, RouterModule.forChild([HOME_ROUTE]), ChatModule],
  declarations: [HomeComponent],
})
export class HomeModule {}
