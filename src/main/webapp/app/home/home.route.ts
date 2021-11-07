import { Route } from '@angular/router';

import { HomeComponent } from './home.component';
import { ChatComponent } from '../chatBot/chat.component';

export const HOME_ROUTE: Route = {
  path: '',
  component: ChatComponent,
  data: {
    pageTitle: 'Welcome, Java Hipster!',
  },
};
