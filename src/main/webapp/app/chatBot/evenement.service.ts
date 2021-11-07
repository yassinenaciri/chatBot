import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mess } from './message';
import { ApplicationConfigService } from '../core/config/application-config.service';

@Injectable({
  providedIn: 'root',
})
export class EvenementService {
  protected baseUrl = this.applicationConfigService.getEndpointFor('api/chat');
  private baseUrl1 = 'http://localhost:8080/api/chat/';

  constructor(private http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(mess: Mess): Observable<Mess> {
    //console.log(data)
    return this.http.post<Mess>(this.baseUrl, mess);
  }

  /*get(): Observable<string> {
    console.log(this.http.get<string>(this.baseUrl))
    return this.http.get<string>(this.baseUrl);
  }*/

  /*getAll(): Observable<Evenement[]> {
    return this.http.get<Evenement[]>(baseUrl);
  }*/
}
