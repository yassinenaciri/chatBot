import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEvenement } from '../evenement.model';
import { EvenementService } from '../service/evenement.service';

@Component({
  templateUrl: './evenement-delete-dialog.component.html',
})
export class EvenementDeleteDialogComponent {
  evenement?: IEvenement;

  constructor(protected evenementService: EvenementService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.evenementService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
