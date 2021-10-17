import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEvenement, Evenement } from '../evenement.model';

import { EvenementService } from './evenement.service';

describe('Evenement Service', () => {
  let service: EvenementService;
  let httpMock: HttpTestingController;
  let elemDefault: IEvenement;
  let expectedResult: IEvenement | IEvenement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EvenementService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      titre: 'AAAAAAA',
      description: 'AAAAAAA',
      localisation: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Evenement', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Evenement()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Evenement', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          titre: 'BBBBBB',
          description: 'BBBBBB',
          localisation: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Evenement', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          localisation: 'BBBBBB',
        },
        new Evenement()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Evenement', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          titre: 'BBBBBB',
          description: 'BBBBBB',
          localisation: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Evenement', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEvenementToCollectionIfMissing', () => {
      it('should add a Evenement to an empty array', () => {
        const evenement: IEvenement = { id: 'ABC' };
        expectedResult = service.addEvenementToCollectionIfMissing([], evenement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(evenement);
      });

      it('should not add a Evenement to an array that contains it', () => {
        const evenement: IEvenement = { id: 'ABC' };
        const evenementCollection: IEvenement[] = [
          {
            ...evenement,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addEvenementToCollectionIfMissing(evenementCollection, evenement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Evenement to an array that doesn't contain it", () => {
        const evenement: IEvenement = { id: 'ABC' };
        const evenementCollection: IEvenement[] = [{ id: 'CBA' }];
        expectedResult = service.addEvenementToCollectionIfMissing(evenementCollection, evenement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(evenement);
      });

      it('should add only unique Evenement to an array', () => {
        const evenementArray: IEvenement[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'f1dfdd68-6939-4a75-ae01-6e892ccdd7b2' }];
        const evenementCollection: IEvenement[] = [{ id: 'ABC' }];
        expectedResult = service.addEvenementToCollectionIfMissing(evenementCollection, ...evenementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const evenement: IEvenement = { id: 'ABC' };
        const evenement2: IEvenement = { id: 'CBA' };
        expectedResult = service.addEvenementToCollectionIfMissing([], evenement, evenement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(evenement);
        expect(expectedResult).toContain(evenement2);
      });

      it('should accept null and undefined values', () => {
        const evenement: IEvenement = { id: 'ABC' };
        expectedResult = service.addEvenementToCollectionIfMissing([], null, evenement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(evenement);
      });

      it('should return initial array if no Evenement is added', () => {
        const evenementCollection: IEvenement[] = [{ id: 'ABC' }];
        expectedResult = service.addEvenementToCollectionIfMissing(evenementCollection, undefined, null);
        expect(expectedResult).toEqual(evenementCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
