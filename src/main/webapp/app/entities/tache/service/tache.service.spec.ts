import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITache, Tache } from '../tache.model';

import { TacheService } from './tache.service';

describe('Tache Service', () => {
  let service: TacheService;
  let httpMock: HttpTestingController;
  let elemDefault: ITache;
  let expectedResult: ITache | ITache[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TacheService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      intitule: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Tache', () => {
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

      service.create(new Tache()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tache', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          intitule: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should partial update a Tache', () => {
      const patchObject = Object.assign(
        {
          intitule: 'BBBBBB',
          dateDebut: currentDate.format(DATE_TIME_FORMAT),
        },
        new Tache()
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

    it('should return a list of Tache', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          intitule: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Tache', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTacheToCollectionIfMissing', () => {
      it('should add a Tache to an empty array', () => {
        const tache: ITache = { id: 'ABC' };
        expectedResult = service.addTacheToCollectionIfMissing([], tache);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tache);
      });

      it('should not add a Tache to an array that contains it', () => {
        const tache: ITache = { id: 'ABC' };
        const tacheCollection: ITache[] = [
          {
            ...tache,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addTacheToCollectionIfMissing(tacheCollection, tache);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tache to an array that doesn't contain it", () => {
        const tache: ITache = { id: 'ABC' };
        const tacheCollection: ITache[] = [{ id: 'CBA' }];
        expectedResult = service.addTacheToCollectionIfMissing(tacheCollection, tache);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tache);
      });

      it('should add only unique Tache to an array', () => {
        const tacheArray: ITache[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'f6fed9ba-065b-40da-be79-997118bb89ae' }];
        const tacheCollection: ITache[] = [{ id: 'ABC' }];
        expectedResult = service.addTacheToCollectionIfMissing(tacheCollection, ...tacheArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tache: ITache = { id: 'ABC' };
        const tache2: ITache = { id: 'CBA' };
        expectedResult = service.addTacheToCollectionIfMissing([], tache, tache2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tache);
        expect(expectedResult).toContain(tache2);
      });

      it('should accept null and undefined values', () => {
        const tache: ITache = { id: 'ABC' };
        expectedResult = service.addTacheToCollectionIfMissing([], null, tache, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tache);
      });

      it('should return initial array if no Tache is added', () => {
        const tacheCollection: ITache[] = [{ id: 'ABC' }];
        expectedResult = service.addTacheToCollectionIfMissing(tacheCollection, undefined, null);
        expect(expectedResult).toEqual(tacheCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
