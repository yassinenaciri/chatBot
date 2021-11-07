import { LOCALE_ID, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { CHAT_ROUTE } from './chat.route';
import { ChatComponent } from './chat.component';
import { ChatModule } from '@progress/kendo-angular-conversational-ui';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import * as likelySubtags from 'cldr-data/supplemental/likelySubtags.json';
import * as weekData from 'cldr-data/supplemental/weekData.json';
import * as currencyData from 'cldr-data/supplemental/currencyData.json';
import * as numbers from 'cldr-data/main/fr/numbers.json';
import * as timeZoneNames from 'cldr-data/main/fr/timeZoneNames.json';
import * as calendar from 'cldr-data/main/fr/ca-gregorian.json';
import * as currencies from 'cldr-data/main/fr/currencies.json';
import * as dateFields from 'cldr-data/main/fr/dateFields.json';
import { load } from '@progress/kendo-angular-intl';
import { ChatService } from './evenement.service';
load(likelySubtags, weekData, currencyData, numbers, currencies, calendar, dateFields, timeZoneNames);
@NgModule({
  imports: [BrowserModule, BrowserAnimationsModule, SharedModule, RouterModule.forChild([CHAT_ROUTE]), ChatModule],
  declarations: [ChatComponent],
  providers: [ChatService, { provide: LOCALE_ID, useValue: 'fr' }],
})
export class chatModule {}
