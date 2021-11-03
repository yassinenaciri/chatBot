import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITache } from '../tache.model';
import { TacheService } from '../service/tache.service';
import { TacheDeleteDialogComponent } from '../delete/tache-delete-dialog.component';

@Component({
  selector: 'jhi-tache',
  templateUrl: './tache.component.html',
})
export class TacheComponent implements OnInit {
  taches?: ITache[];
  isLoading = false;

  constructor(protected tacheService: TacheService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.tacheService.query().subscribe(
      (res: HttpResponse<ITache[]>) => {
        this.isLoading = false;
        this.taches = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITache): string {
    return item.id!;
  }

  delete(tache: ITache): void {
    const modalRef = this.modalService.open(TacheDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tache = tache;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
