import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CreneauxComponent } from './list/creneaux.component';
import { CreneauxDetailComponent } from './detail/creneaux-detail.component';
import { CreneauxUpdateComponent } from './update/creneaux-update.component';
import { CreneauxDeleteDialogComponent } from './delete/creneaux-delete-dialog.component';
import { CreneauxRoutingModule } from './route/creneaux-routing.module';

@NgModule({
  imports: [SharedModule, CreneauxRoutingModule],
  declarations: [CreneauxComponent, CreneauxDetailComponent, CreneauxUpdateComponent, CreneauxDeleteDialogComponent],
  entryComponents: [CreneauxDeleteDialogComponent],
})
export class CreneauxModule {}
