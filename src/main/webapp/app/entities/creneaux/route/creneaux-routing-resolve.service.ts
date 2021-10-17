import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICreneaux, Creneaux } from '../creneaux.model';
import { CreneauxService } from '../service/creneaux.service';

@Injectable({ providedIn: 'root' })
export class CreneauxRoutingResolveService implements Resolve<ICreneaux> {
  constructor(protected service: CreneauxService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICreneaux> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((creneaux: HttpResponse<Creneaux>) => {
          if (creneaux.body) {
            return of(creneaux.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Creneaux());
  }
}
