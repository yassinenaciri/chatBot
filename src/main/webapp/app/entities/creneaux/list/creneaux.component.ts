import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICreneaux } from '../creneaux.model';
import { CreneauxService } from '../service/creneaux.service';
import { CreneauxDeleteDialogComponent } from '../delete/creneaux-delete-dialog.component';

@Component({
  selector: 'jhi-creneaux',
  templateUrl: './creneaux.component.html',
})
export class CreneauxComponent implements OnInit {
  creneaux?: ICreneaux[];
  isLoading = false;

  constructor(protected creneauxService: CreneauxService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.creneauxService.query().subscribe(
      (res: HttpResponse<ICreneaux[]>) => {
        this.isLoading = false;
        this.creneaux = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICreneaux): string {
    return item.id!;
  }

  delete(creneaux: ICreneaux): void {
    const modalRef = this.modalService.open(CreneauxDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.creneaux = creneaux;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
