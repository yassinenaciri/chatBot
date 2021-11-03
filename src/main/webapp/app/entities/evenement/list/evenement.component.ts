import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEvenement } from '../evenement.model';
import { EvenementService } from '../service/evenement.service';
import { EvenementDeleteDialogComponent } from '../delete/evenement-delete-dialog.component';

@Component({
  selector: 'jhi-evenement',
  templateUrl: './evenement.component.html',
})
export class EvenementComponent implements OnInit {
  evenements?: IEvenement[];
  isLoading = false;

  constructor(protected evenementService: EvenementService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.evenementService.query().subscribe(
      (res: HttpResponse<IEvenement[]>) => {
        this.isLoading = false;
        this.evenements = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IEvenement): string {
    return item.id!;
  }

  delete(evenement: IEvenement): void {
    const modalRef = this.modalService.open(EvenementDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.evenement = evenement;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
