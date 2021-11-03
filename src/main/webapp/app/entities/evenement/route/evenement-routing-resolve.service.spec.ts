jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IEvenement, Evenement } from '../evenement.model';
import { EvenementService } from '../service/evenement.service';

import { EvenementRoutingResolveService } from './evenement-routing-resolve.service';

describe('Evenement routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: EvenementRoutingResolveService;
  let service: EvenementService;
  let resultEvenement: IEvenement | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(EvenementRoutingResolveService);
    service = TestBed.inject(EvenementService);
    resultEvenement = undefined;
  });

  describe('resolve', () => {
    it('should return IEvenement returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEvenement = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultEvenement).toEqual({ id: 'ABC' });
    });

    it('should return new IEvenement if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEvenement = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultEvenement).toEqual(new Evenement());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Evenement })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEvenement = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultEvenement).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
