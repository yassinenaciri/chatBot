import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CreneauxComponent } from '../list/creneaux.component';
import { CreneauxDetailComponent } from '../detail/creneaux-detail.component';
import { CreneauxUpdateComponent } from '../update/creneaux-update.component';
import { CreneauxRoutingResolveService } from './creneaux-routing-resolve.service';

const creneauxRoute: Routes = [
  {
    path: '',
    component: CreneauxComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CreneauxDetailComponent,
    resolve: {
      creneaux: CreneauxRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CreneauxUpdateComponent,
    resolve: {
      creneaux: CreneauxRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CreneauxUpdateComponent,
    resolve: {
      creneaux: CreneauxRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(creneauxRoute)],
  exports: [RouterModule],
})
export class CreneauxRoutingModule {}
