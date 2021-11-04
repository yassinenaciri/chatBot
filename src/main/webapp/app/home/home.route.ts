import { Route } from '@angular/router';

import { HomeComponent } from './home.component';
import { UtilisateurComponent } from '../entities/utilisateur/list/utilisateur.component';

export const HOME_ROUTE: Route = {
  path: '',
  component: UtilisateurComponent,
  data: {
    pageTitle: 'Welcome, Java Hipster!',
  },
};
