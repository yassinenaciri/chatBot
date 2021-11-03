import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EvenementComponent } from '../list/evenement.component';
import { EvenementDetailComponent } from '../detail/evenement-detail.component';
import { EvenementUpdateComponent } from '../update/evenement-update.component';
import { EvenementRoutingResolveService } from './evenement-routing-resolve.service';

const evenementRoute: Routes = [
  {
    path: '',
    component: EvenementComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EvenementDetailComponent,
    resolve: {
      evenement: EvenementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EvenementUpdateComponent,
    resolve: {
      evenement: EvenementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EvenementUpdateComponent,
    resolve: {
      evenement: EvenementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(evenementRoute)],
  exports: [RouterModule],
})
export class EvenementRoutingModule {}
