import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITache, Tache } from '../tache.model';
import { TacheService } from '../service/tache.service';

@Injectable({ providedIn: 'root' })
export class TacheRoutingResolveService implements Resolve<ITache> {
  constructor(protected service: TacheService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITache> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tache: HttpResponse<Tache>) => {
          if (tache.body) {
            return of(tache.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tache());
  }
}
