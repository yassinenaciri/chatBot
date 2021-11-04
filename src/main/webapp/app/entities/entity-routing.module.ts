import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'utilisateur',
        data: { pageTitle: 'Utilisateurs' },
        loadChildren: () => import('./utilisateur/utilisateur.module').then(m => m.UtilisateurModule),
      },
      {
        path: 'tache',
        data: { pageTitle: 'Taches' },
        loadChildren: () => import('./tache/tache.module').then(m => m.TacheModule),
      },
    ]),
  ],
})
export class EntityRoutingModule {}
