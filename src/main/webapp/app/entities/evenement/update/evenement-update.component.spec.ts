jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { EvenementService } from '../service/evenement.service';
import { IEvenement, Evenement } from '../evenement.model';
import { ICreneaux } from 'app/entities/creneaux/creneaux.model';
import { CreneauxService } from 'app/entities/creneaux/service/creneaux.service';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';

import { EvenementUpdateComponent } from './evenement-update.component';

describe('Component Tests', () => {
  describe('Evenement Management Update Component', () => {
    let comp: EvenementUpdateComponent;
    let fixture: ComponentFixture<EvenementUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let evenementService: EvenementService;
    let creneauxService: CreneauxService;
    let utilisateurService: UtilisateurService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [EvenementUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(EvenementUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EvenementUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      evenementService = TestBed.inject(EvenementService);
      creneauxService = TestBed.inject(CreneauxService);
      utilisateurService = TestBed.inject(UtilisateurService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call creneaux query and add missing value', () => {
        const evenement: IEvenement = { id: 'CBA' };
        const creneaux: ICreneaux = { id: 'a169e351-8620-4783-966b-03c0179cf387' };
        evenement.creneaux = creneaux;

        const creneauxCollection: ICreneaux[] = [{ id: '0a2aa9ac-9fd1-4648-8221-986e6ba86e00' }];
        jest.spyOn(creneauxService, 'query').mockReturnValue(of(new HttpResponse({ body: creneauxCollection })));
        const expectedCollection: ICreneaux[] = [creneaux, ...creneauxCollection];
        jest.spyOn(creneauxService, 'addCreneauxToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ evenement });
        comp.ngOnInit();

        expect(creneauxService.query).toHaveBeenCalled();
        expect(creneauxService.addCreneauxToCollectionIfMissing).toHaveBeenCalledWith(creneauxCollection, creneaux);
        expect(comp.creneauxCollection).toEqual(expectedCollection);
      });

      it('Should call Utilisateur query and add missing value', () => {
        const evenement: IEvenement = { id: 'CBA' };
        const employee: IUtilisateur = { id: '472511db-35a8-4935-9d47-036b33ab75b1' };
        evenement.employee = employee;

        const utilisateurCollection: IUtilisateur[] = [{ id: '42d0d352-f4b1-4a08-ace7-408f1c405092' }];
        jest.spyOn(utilisateurService, 'query').mockReturnValue(of(new HttpResponse({ body: utilisateurCollection })));
        const additionalUtilisateurs = [employee];
        const expectedCollection: IUtilisateur[] = [...additionalUtilisateurs, ...utilisateurCollection];
        jest.spyOn(utilisateurService, 'addUtilisateurToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ evenement });
        comp.ngOnInit();

        expect(utilisateurService.query).toHaveBeenCalled();
        expect(utilisateurService.addUtilisateurToCollectionIfMissing).toHaveBeenCalledWith(
          utilisateurCollection,
          ...additionalUtilisateurs
        );
        expect(comp.utilisateursSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const evenement: IEvenement = { id: 'CBA' };
        const creneaux: ICreneaux = { id: '960030f3-d26b-445b-bac0-c7a0a5949405' };
        evenement.creneaux = creneaux;
        const employee: IUtilisateur = { id: 'd7a04117-a7b3-4193-be6b-f944c20a0e4e' };
        evenement.employee = employee;

        activatedRoute.data = of({ evenement });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(evenement));
        expect(comp.creneauxCollection).toContain(creneaux);
        expect(comp.utilisateursSharedCollection).toContain(employee);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Evenement>>();
        const evenement = { id: 'ABC' };
        jest.spyOn(evenementService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ evenement });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: evenement }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(evenementService.update).toHaveBeenCalledWith(evenement);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Evenement>>();
        const evenement = new Evenement();
        jest.spyOn(evenementService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ evenement });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: evenement }));
        saveSubject.complete();

        // THEN
        expect(evenementService.create).toHaveBeenCalledWith(evenement);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Evenement>>();
        const evenement = { id: 'ABC' };
        jest.spyOn(evenementService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ evenement });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(evenementService.update).toHaveBeenCalledWith(evenement);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCreneauxById', () => {
        it('Should return tracked Creneaux primary key', () => {
          const entity = { id: 'ABC' };
          const trackResult = comp.trackCreneauxById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackUtilisateurById', () => {
        it('Should return tracked Utilisateur primary key', () => {
          const entity = { id: 'ABC' };
          const trackResult = comp.trackUtilisateurById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
