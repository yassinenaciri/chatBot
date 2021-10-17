import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICreneaux, Creneaux } from '../creneaux.model';

import { CreneauxService } from './creneaux.service';

describe('Creneaux Service', () => {
  let service: CreneauxService;
  let httpMock: HttpTestingController;
  let elemDefault: ICreneaux;
  let expectedResult: ICreneaux | ICreneaux[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CreneauxService);
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

    it('should create a Creneaux', () => {
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

      service.create(new Creneaux()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Creneaux', () => {
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

    it('should partial update a Creneaux', () => {
      const patchObject = Object.assign(
        {
          dateDebut: currentDate.format(DATE_TIME_FORMAT),
        },
        new Creneaux()
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

    it('should return a list of Creneaux', () => {
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

    it('should delete a Creneaux', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCreneauxToCollectionIfMissing', () => {
      it('should add a Creneaux to an empty array', () => {
        const creneaux: ICreneaux = { id: 'ABC' };
        expectedResult = service.addCreneauxToCollectionIfMissing([], creneaux);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(creneaux);
      });

      it('should not add a Creneaux to an array that contains it', () => {
        const creneaux: ICreneaux = { id: 'ABC' };
        const creneauxCollection: ICreneaux[] = [
          {
            ...creneaux,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCreneauxToCollectionIfMissing(creneauxCollection, creneaux);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Creneaux to an array that doesn't contain it", () => {
        const creneaux: ICreneaux = { id: 'ABC' };
        const creneauxCollection: ICreneaux[] = [{ id: 'CBA' }];
        expectedResult = service.addCreneauxToCollectionIfMissing(creneauxCollection, creneaux);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(creneaux);
      });

      it('should add only unique Creneaux to an array', () => {
        const creneauxArray: ICreneaux[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'b73df61a-9e98-4acc-aae1-d3df327707b3' }];
        const creneauxCollection: ICreneaux[] = [{ id: 'ABC' }];
        expectedResult = service.addCreneauxToCollectionIfMissing(creneauxCollection, ...creneauxArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const creneaux: ICreneaux = { id: 'ABC' };
        const creneaux2: ICreneaux = { id: 'CBA' };
        expectedResult = service.addCreneauxToCollectionIfMissing([], creneaux, creneaux2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(creneaux);
        expect(expectedResult).toContain(creneaux2);
      });

      it('should accept null and undefined values', () => {
        const creneaux: ICreneaux = { id: 'ABC' };
        expectedResult = service.addCreneauxToCollectionIfMissing([], null, creneaux, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(creneaux);
      });

      it('should return initial array if no Creneaux is added', () => {
        const creneauxCollection: ICreneaux[] = [{ id: 'ABC' }];
        expectedResult = service.addCreneauxToCollectionIfMissing(creneauxCollection, undefined, null);
        expect(expectedResult).toEqual(creneauxCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
