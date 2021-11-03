import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'evenement',
        data: { pageTitle: 'Evenements' },
        loadChildren: () => import('./evenement/evenement.module').then(m => m.EvenementModule),
      },
      {
        path: 'creneaux',
        data: { pageTitle: 'Creneaux' },
        loadChildren: () => import('./creneaux/creneaux.module').then(m => m.CreneauxModule),
      },
      {
        path: 'utilisateur',
        data: { pageTitle: 'Utilisateurs' },
        loadChildren: () => import('./utilisateur/utilisateur.module').then(m => m.UtilisateurModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
