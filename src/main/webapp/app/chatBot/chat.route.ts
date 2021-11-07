import { Route } from '@angular/router';
import { ChatComponent } from './chat.component';

export const CHAT_ROUTE: Route = {
  path: 'chat',
  component: ChatComponent,
  data: {
    pageTitle: 'chatBot',
  },
};
