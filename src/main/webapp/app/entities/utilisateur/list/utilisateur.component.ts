import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUtilisateur } from '../utilisateur.model';
import { UtilisateurService } from '../service/utilisateur.service';
import { UtilisateurDeleteDialogComponent } from '../delete/utilisateur-delete-dialog.component';

@Component({
  selector: 'jhi-utilisateur',
  templateUrl: './utilisateur.component.html',
})
export class UtilisateurComponent implements OnInit {
  utilisateurs?: IUtilisateur[];
  isLoading = false;

  constructor(protected utilisateurService: UtilisateurService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.utilisateurService.query().subscribe(
      (res: HttpResponse<IUtilisateur[]>) => {
        this.isLoading = false;
        this.utilisateurs = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IUtilisateur): string {
    return item.id!;
  }

  delete(utilisateur: IUtilisateur): void {
    const modalRef = this.modalService.open(UtilisateurDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.utilisateur = utilisateur;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
