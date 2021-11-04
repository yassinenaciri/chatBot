import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mess } from './message';

@Injectable({
  providedIn: 'root',
})
export class EvenementService {
  private baseUrl: string = 'http://localhost:8080/home/';

  constructor(private http: HttpClient) {}

  create(mess: Mess) {
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
