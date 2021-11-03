import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITache, getTacheIdentifier } from '../tache.model';

export type EntityResponseType = HttpResponse<ITache>;
export type EntityArrayResponseType = HttpResponse<ITache[]>;

@Injectable({ providedIn: 'root' })
export class TacheService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/taches');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tache: ITache): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tache);
    return this.http
      .post<ITache>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(tache: ITache): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tache);
    return this.http
      .put<ITache>(`${this.resourceUrl}/${getTacheIdentifier(tache) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(tache: ITache): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tache);
    return this.http
      .patch<ITache>(`${this.resourceUrl}/${getTacheIdentifier(tache) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<ITache>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITache[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTacheToCollectionIfMissing(tacheCollection: ITache[], ...tachesToCheck: (ITache | null | undefined)[]): ITache[] {
    const taches: ITache[] = tachesToCheck.filter(isPresent);
    if (taches.length > 0) {
      const tacheCollectionIdentifiers = tacheCollection.map(tacheItem => getTacheIdentifier(tacheItem)!);
      const tachesToAdd = taches.filter(tacheItem => {
        const tacheIdentifier = getTacheIdentifier(tacheItem);
        if (tacheIdentifier == null || tacheCollectionIdentifiers.includes(tacheIdentifier)) {
          return false;
        }
        tacheCollectionIdentifiers.push(tacheIdentifier);
        return true;
      });
      return [...tachesToAdd, ...tacheCollection];
    }
    return tacheCollection;
  }

  protected convertDateFromClient(tache: ITache): ITache {
    return Object.assign({}, tache, {
      dateDebut: tache.dateDebut?.isValid() ? tache.dateDebut.toJSON() : undefined,
      dateFin: tache.dateFin?.isValid() ? tache.dateFin.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateDebut = res.body.dateDebut ? dayjs(res.body.dateDebut) : undefined;
      res.body.dateFin = res.body.dateFin ? dayjs(res.body.dateFin) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((tache: ITache) => {
        tache.dateDebut = tache.dateDebut ? dayjs(tache.dateDebut) : undefined;
        tache.dateFin = tache.dateFin ? dayjs(tache.dateFin) : undefined;
      });
    }
    return res;
  }
}
