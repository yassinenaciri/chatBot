jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CreneauxService } from '../service/creneaux.service';
import { ICreneaux, Creneaux } from '../creneaux.model';

import { CreneauxUpdateComponent } from './creneaux-update.component';

describe('Component Tests', () => {
  describe('Creneaux Management Update Component', () => {
    let comp: CreneauxUpdateComponent;
    let fixture: ComponentFixture<CreneauxUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let creneauxService: CreneauxService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CreneauxUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CreneauxUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CreneauxUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      creneauxService = TestBed.inject(CreneauxService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const creneaux: ICreneaux = { id: 'CBA' };

        activatedRoute.data = of({ creneaux });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(creneaux));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Creneaux>>();
        const creneaux = { id: 'ABC' };
        jest.spyOn(creneauxService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ creneaux });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: creneaux }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(creneauxService.update).toHaveBeenCalledWith(creneaux);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Creneaux>>();
        const creneaux = new Creneaux();
        jest.spyOn(creneauxService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ creneaux });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: creneaux }));
        saveSubject.complete();

        // THEN
        expect(creneauxService.create).toHaveBeenCalledWith(creneaux);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Creneaux>>();
        const creneaux = { id: 'ABC' };
        jest.spyOn(creneauxService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ creneaux });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(creneauxService.update).toHaveBeenCalledWith(creneaux);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
