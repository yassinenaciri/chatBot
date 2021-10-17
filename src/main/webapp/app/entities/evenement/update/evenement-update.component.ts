import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEvenement, Evenement } from '../evenement.model';
import { EvenementService } from '../service/evenement.service';
import { ICreneaux } from 'app/entities/creneaux/creneaux.model';
import { CreneauxService } from 'app/entities/creneaux/service/creneaux.service';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';

@Component({
  selector: 'jhi-evenement-update',
  templateUrl: './evenement-update.component.html',
})
export class EvenementUpdateComponent implements OnInit {
  isSaving = false;

  creneauxCollection: ICreneaux[] = [];
  utilisateursSharedCollection: IUtilisateur[] = [];

  editForm = this.fb.group({
    id: [],
    titre: [null, [Validators.required]],
    description: [],
    localisation: [],
    creneaux: [],
    employee: [],
  });

  constructor(
    protected evenementService: EvenementService,
    protected creneauxService: CreneauxService,
    protected utilisateurService: UtilisateurService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evenement }) => {
      this.updateForm(evenement);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const evenement = this.createFromForm();
    if (evenement.id !== undefined) {
      this.subscribeToSaveResponse(this.evenementService.update(evenement));
    } else {
      this.subscribeToSaveResponse(this.evenementService.create(evenement));
    }
  }

  trackCreneauxById(index: number, item: ICreneaux): string {
    return item.id!;
  }

  trackUtilisateurById(index: number, item: IUtilisateur): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvenement>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(evenement: IEvenement): void {
    this.editForm.patchValue({
      id: evenement.id,
      titre: evenement.titre,
      description: evenement.description,
      localisation: evenement.localisation,
      creneaux: evenement.creneaux,
      employee: evenement.employee,
    });

    this.creneauxCollection = this.creneauxService.addCreneauxToCollectionIfMissing(this.creneauxCollection, evenement.creneaux);
    this.utilisateursSharedCollection = this.utilisateurService.addUtilisateurToCollectionIfMissing(
      this.utilisateursSharedCollection,
      evenement.employee
    );
  }

  protected loadRelationshipsOptions(): void {
    this.creneauxService
      .query({ filter: 'evenement-is-null' })
      .pipe(map((res: HttpResponse<ICreneaux[]>) => res.body ?? []))
      .pipe(
        map((creneaux: ICreneaux[]) =>
          this.creneauxService.addCreneauxToCollectionIfMissing(creneaux, this.editForm.get('creneaux')!.value)
        )
      )
      .subscribe((creneaux: ICreneaux[]) => (this.creneauxCollection = creneaux));

    this.utilisateurService
      .query()
      .pipe(map((res: HttpResponse<IUtilisateur[]>) => res.body ?? []))
      .pipe(
        map((utilisateurs: IUtilisateur[]) =>
          this.utilisateurService.addUtilisateurToCollectionIfMissing(utilisateurs, this.editForm.get('employee')!.value)
        )
      )
      .subscribe((utilisateurs: IUtilisateur[]) => (this.utilisateursSharedCollection = utilisateurs));
  }

  protected createFromForm(): IEvenement {
    return {
      ...new Evenement(),
      id: this.editForm.get(['id'])!.value,
      titre: this.editForm.get(['titre'])!.value,
      description: this.editForm.get(['description'])!.value,
      localisation: this.editForm.get(['localisation'])!.value,
      creneaux: this.editForm.get(['creneaux'])!.value,
      employee: this.editForm.get(['employee'])!.value,
    };
  }
}
