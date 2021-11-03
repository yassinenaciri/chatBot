jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICreneaux, Creneaux } from '../creneaux.model';
import { CreneauxService } from '../service/creneaux.service';

import { CreneauxRoutingResolveService } from './creneaux-routing-resolve.service';

describe('Creneaux routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CreneauxRoutingResolveService;
  let service: CreneauxService;
  let resultCreneaux: ICreneaux | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CreneauxRoutingResolveService);
    service = TestBed.inject(CreneauxService);
    resultCreneaux = undefined;
  });

  describe('resolve', () => {
    it('should return ICreneaux returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCreneaux = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCreneaux).toEqual({ id: 'ABC' });
    });

    it('should return new ICreneaux if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCreneaux = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCreneaux).toEqual(new Creneaux());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Creneaux })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCreneaux = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCreneaux).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
