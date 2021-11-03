import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICreneaux } from '../creneaux.model';

@Component({
  selector: 'jhi-creneaux-detail',
  templateUrl: './creneaux-detail.component.html',
})
export class CreneauxDetailComponent implements OnInit {
  creneaux: ICreneaux | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ creneaux }) => {
      this.creneaux = creneaux;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
