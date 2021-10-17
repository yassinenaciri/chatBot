import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICreneaux, getCreneauxIdentifier } from '../creneaux.model';

export type EntityResponseType = HttpResponse<ICreneaux>;
export type EntityArrayResponseType = HttpResponse<ICreneaux[]>;

@Injectable({ providedIn: 'root' })
export class CreneauxService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/creneaux');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(creneaux: ICreneaux): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(creneaux);
    return this.http
      .post<ICreneaux>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(creneaux: ICreneaux): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(creneaux);
    return this.http
      .put<ICreneaux>(`${this.resourceUrl}/${getCreneauxIdentifier(creneaux) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(creneaux: ICreneaux): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(creneaux);
    return this.http
      .patch<ICreneaux>(`${this.resourceUrl}/${getCreneauxIdentifier(creneaux) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<ICreneaux>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICreneaux[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCreneauxToCollectionIfMissing(creneauxCollection: ICreneaux[], ...creneauxToCheck: (ICreneaux | null | undefined)[]): ICreneaux[] {
    const creneaux: ICreneaux[] = creneauxToCheck.filter(isPresent);
    if (creneaux.length > 0) {
      const creneauxCollectionIdentifiers = creneauxCollection.map(creneauxItem => getCreneauxIdentifier(creneauxItem)!);
      const creneauxToAdd = creneaux.filter(creneauxItem => {
        const creneauxIdentifier = getCreneauxIdentifier(creneauxItem);
        if (creneauxIdentifier == null || creneauxCollectionIdentifiers.includes(creneauxIdentifier)) {
          return false;
        }
        creneauxCollectionIdentifiers.push(creneauxIdentifier);
        return true;
      });
      return [...creneauxToAdd, ...creneauxCollection];
    }
    return creneauxCollection;
  }

  protected convertDateFromClient(creneaux: ICreneaux): ICreneaux {
    return Object.assign({}, creneaux, {
      dateDebut: creneaux.dateDebut?.isValid() ? creneaux.dateDebut.toJSON() : undefined,
      dateFin: creneaux.dateFin?.isValid() ? creneaux.dateFin.toJSON() : undefined,
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
      res.body.forEach((creneaux: ICreneaux) => {
        creneaux.dateDebut = creneaux.dateDebut ? dayjs(creneaux.dateDebut) : undefined;
        creneaux.dateFin = creneaux.dateFin ? dayjs(creneaux.dateFin) : undefined;
      });
    }
    return res;
  }
}
