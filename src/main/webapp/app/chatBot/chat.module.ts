import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { CHAT_ROUTE } from './chat.route';
import { ChatComponent } from './chat.component';
import { ChatModule } from '@progress/kendo-angular-conversational-ui';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  imports: [BrowserModule, BrowserAnimationsModule, SharedModule, RouterModule.forChild([CHAT_ROUTE]), ChatModule],
  declarations: [ChatComponent],
  bootstrap: [ChatComponent],
})
export class chatModule {}
