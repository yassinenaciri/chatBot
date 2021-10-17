import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICreneaux } from '../creneaux.model';
import { CreneauxService } from '../service/creneaux.service';

@Component({
  templateUrl: './creneaux-delete-dialog.component.html',
})
export class CreneauxDeleteDialogComponent {
  creneaux?: ICreneaux;

  constructor(protected creneauxService: CreneauxService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.creneauxService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
