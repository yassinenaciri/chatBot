import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EvenementComponent } from './list/evenement.component';
import { EvenementDetailComponent } from './detail/evenement-detail.component';
import { EvenementUpdateComponent } from './update/evenement-update.component';
import { EvenementDeleteDialogComponent } from './delete/evenement-delete-dialog.component';
import { EvenementRoutingModule } from './route/evenement-routing.module';

@NgModule({
  imports: [SharedModule, EvenementRoutingModule],
  declarations: [EvenementComponent, EvenementDetailComponent, EvenementUpdateComponent, EvenementDeleteDialogComponent],
  entryComponents: [EvenementDeleteDialogComponent],
})
export class EvenementModule {}
