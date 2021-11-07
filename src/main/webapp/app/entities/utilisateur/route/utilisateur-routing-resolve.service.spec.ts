jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IUtilisateur, Utilisateur } from '../utilisateur.model';
import { UtilisateurService } from '../service/utilisateur.service';

import { UtilisateurRoutingResolveService } from './utilisateur-routing-resolve.service';

describe('Utilisateur routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: UtilisateurRoutingResolveService;
  let service: UtilisateurService;
  let resultUtilisateur: IUtilisateur | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(UtilisateurRoutingResolveService);
    service = TestBed.inject(UtilisateurService);
    resultUtilisateur = undefined;
  });

  describe('resolve', () => {
    it('should return IUtilisateur returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUtilisateur = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultUtilisateur).toEqual({ id: 'ABC' });
    });

    it('should return new IUtilisateur if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUtilisateur = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultUtilisateur).toEqual(new Utilisateur());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Utilisateur })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUtilisateur = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultUtilisateur).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});