import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICreneaux, Creneaux } from '../creneaux.model';
import { CreneauxService } from '../service/creneaux.service';

@Component({
  selector: 'jhi-creneaux-update',
  templateUrl: './creneaux-update.component.html',
})
export class CreneauxUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    dateDebut: [null, [Validators.required]],
    dateFin: [],
  });

  constructor(protected creneauxService: CreneauxService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ creneaux }) => {
      if (creneaux.id === undefined) {
        const today = dayjs().startOf('day');
        creneaux.dateDebut = today;
        creneaux.dateFin = today;
      }

      this.updateForm(creneaux);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const creneaux = this.createFromForm();
    if (creneaux.id !== undefined) {
      this.subscribeToSaveResponse(this.creneauxService.update(creneaux));
    } else {
      this.subscribeToSaveResponse(this.creneauxService.create(creneaux));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICreneaux>>): void {
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

  protected updateForm(creneaux: ICreneaux): void {
    this.editForm.patchValue({
      id: creneaux.id,
      dateDebut: creneaux.dateDebut ? creneaux.dateDebut.format(DATE_TIME_FORMAT) : null,
      dateFin: creneaux.dateFin ? creneaux.dateFin.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): ICreneaux {
    return {
      ...new Creneaux(),
      id: this.editForm.get(['id'])!.value,
      dateDebut: this.editForm.get(['dateDebut'])!.value ? dayjs(this.editForm.get(['dateDebut'])!.value, DATE_TIME_FORMAT) : undefined,
      dateFin: this.editForm.get(['dateFin'])!.value ? dayjs(this.editForm.get(['dateFin'])!.value, DATE_TIME_FORMAT) : undefined,
    };
  }
}
