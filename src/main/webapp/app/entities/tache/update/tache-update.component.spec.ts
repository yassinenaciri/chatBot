jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TacheService } from '../service/tache.service';
import { ITache, Tache } from '../tache.model';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';

import { TacheUpdateComponent } from './tache-update.component';

describe('Tache Management Update Component', () => {
  let comp: TacheUpdateComponent;
  let fixture: ComponentFixture<TacheUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tacheService: TacheService;
  let utilisateurService: UtilisateurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TacheUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(TacheUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TacheUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tacheService = TestBed.inject(TacheService);
    utilisateurService = TestBed.inject(UtilisateurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Utilisateur query and add missing value', () => {
      const tache: ITache = { id: 'CBA' };
      const utilisateur: IUtilisateur = { id: '5cc5913f-635e-442b-b1ca-106235bed747' };
      tache.utilisateur = utilisateur;

      const utilisateurCollection: IUtilisateur[] = [{ id: '9538c428-6349-4981-97a6-8475c7151911' }];
      jest.spyOn(utilisateurService, 'query').mockReturnValue(of(new HttpResponse({ body: utilisateurCollection })));
      const additionalUtilisateurs = [utilisateur];
      const expectedCollection: IUtilisateur[] = [...additionalUtilisateurs, ...utilisateurCollection];
      jest.spyOn(utilisateurService, 'addUtilisateurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(utilisateurService.query).toHaveBeenCalled();
      expect(utilisateurService.addUtilisateurToCollectionIfMissing).toHaveBeenCalledWith(utilisateurCollection, ...additionalUtilisateurs);
      expect(comp.utilisateursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tache: ITache = { id: 'CBA' };
      const utilisateur: IUtilisateur = { id: '141fd587-1e8d-4007-b588-e135d40ead0f' };
      tache.utilisateur = utilisateur;

      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tache));
      expect(comp.utilisateursSharedCollection).toContain(utilisateur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = { id: 'ABC' };
      jest.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tache }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tacheService.update).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = new Tache();
      jest.spyOn(tacheService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tache }));
      saveSubject.complete();

      // THEN
      expect(tacheService.create).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tache>>();
      const tache = { id: 'ABC' };
      jest.spyOn(tacheService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tache });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tacheService.update).toHaveBeenCalledWith(tache);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUtilisateurById', () => {
      it('Should return tracked Utilisateur primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackUtilisateurById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
