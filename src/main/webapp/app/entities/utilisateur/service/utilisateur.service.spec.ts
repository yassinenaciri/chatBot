import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUtilisateur, Utilisateur } from '../utilisateur.model';

import { UtilisateurService } from './utilisateur.service';

describe('Utilisateur Service', () => {
  let service: UtilisateurService;
  let httpMock: HttpTestingController;
  let elemDefault: IUtilisateur;
  let expectedResult: IUtilisateur | IUtilisateur[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UtilisateurService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      dateDebut: currentDate,
      dateFin: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateDebut: currentDate.format(DATE_TIME_FORMAT),
          dateFin: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Utilisateur', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          dateDebut: currentDate.format(DATE_TIME_FORMAT),
          dateFin: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.create(new Utilisateur()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Utilisateur', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          dateDebut: currentDate.format(DATE_TIME_FORMAT),
          dateFin: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Utilisateur', () => {
      const patchObject = Object.assign(
        {
          dateFin: currentDate.format(DATE_TIME_FORMAT),
        },
        new Utilisateur()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Utilisateur', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          dateDebut: currentDate.format(DATE_TIME_FORMAT),
          dateFin: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateDebut: currentDate,
          dateFin: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Utilisateur', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUtilisateurToCollectionIfMissing', () => {
      it('should add a Utilisateur to an empty array', () => {
        const utilisateur: IUtilisateur = { id: 'ABC' };
        expectedResult = service.addUtilisateurToCollectionIfMissing([], utilisateur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(utilisateur);
      });

      it('should not add a Utilisateur to an array that contains it', () => {
        const utilisateur: IUtilisateur = { id: 'ABC' };
        const utilisateurCollection: IUtilisateur[] = [
          {
            ...utilisateur,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addUtilisateurToCollectionIfMissing(utilisateurCollection, utilisateur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Utilisateur to an array that doesn't contain it", () => {
        const utilisateur: IUtilisateur = { id: 'ABC' };
        const utilisateurCollection: IUtilisateur[] = [{ id: 'CBA' }];
        expectedResult = service.addUtilisateurToCollectionIfMissing(utilisateurCollection, utilisateur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(utilisateur);
      });

      it('should add only unique Utilisateur to an array', () => {
        const utilisateurArray: IUtilisateur[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '8793d6ed-d377-4064-afb8-4a9c1151a82b' }];
        const utilisateurCollection: IUtilisateur[] = [{ id: 'ABC' }];
        expectedResult = service.addUtilisateurToCollectionIfMissing(utilisateurCollection, ...utilisateurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const utilisateur: IUtilisateur = { id: 'ABC' };
        const utilisateur2: IUtilisateur = { id: 'CBA' };
        expectedResult = service.addUtilisateurToCollectionIfMissing([], utilisateur, utilisateur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(utilisateur);
        expect(expectedResult).toContain(utilisateur2);
      });

      it('should accept null and undefined values', () => {
        const utilisateur: IUtilisateur = { id: 'ABC' };
        expectedResult = service.addUtilisateurToCollectionIfMissing([], null, utilisateur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(utilisateur);
      });

      it('should return initial array if no Utilisateur is added', () => {
        const utilisateurCollection: IUtilisateur[] = [{ id: 'ABC' }];
        expectedResult = service.addUtilisateurToCollectionIfMissing(utilisateurCollection, undefined, null);
        expect(expectedResult).toEqual(utilisateurCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
