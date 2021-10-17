import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEvenement, getEvenementIdentifier } from '../evenement.model';

export type EntityResponseType = HttpResponse<IEvenement>;
export type EntityArrayResponseType = HttpResponse<IEvenement[]>;

@Injectable({ providedIn: 'root' })
export class EvenementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/evenements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(evenement: IEvenement): Observable<EntityResponseType> {
    return this.http.post<IEvenement>(this.resourceUrl, evenement, { observe: 'response' });
  }

  update(evenement: IEvenement): Observable<EntityResponseType> {
    return this.http.put<IEvenement>(`${this.resourceUrl}/${getEvenementIdentifier(evenement) as string}`, evenement, {
      observe: 'response',
    });
  }

  partialUpdate(evenement: IEvenement): Observable<EntityResponseType> {
    return this.http.patch<IEvenement>(`${this.resourceUrl}/${getEvenementIdentifier(evenement) as string}`, evenement, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IEvenement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEvenement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEvenementToCollectionIfMissing(
    evenementCollection: IEvenement[],
    ...evenementsToCheck: (IEvenement | null | undefined)[]
  ): IEvenement[] {
    const evenements: IEvenement[] = evenementsToCheck.filter(isPresent);
    if (evenements.length > 0) {
      const evenementCollectionIdentifiers = evenementCollection.map(evenementItem => getEvenementIdentifier(evenementItem)!);
      const evenementsToAdd = evenements.filter(evenementItem => {
        const evenementIdentifier = getEvenementIdentifier(evenementItem);
        if (evenementIdentifier == null || evenementCollectionIdentifiers.includes(evenementIdentifier)) {
          return false;
        }
        evenementCollectionIdentifiers.push(evenementIdentifier);
        return true;
      });
      return [...evenementsToAdd, ...evenementCollection];
    }
    return evenementCollection;
  }
}
