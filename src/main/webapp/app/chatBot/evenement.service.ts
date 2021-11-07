import { Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Mess } from './message';
import { ApplicationConfigService } from '../core/config/application-config.service';

// Mock remote service

@Injectable()
export class ChatService {
  public readonly responses: Subject<string> = new Subject<string>();

  public create(question: string): void {
    const length = question.length;
    const answer = `"${question}" contains exactly ${length} symbols.`;

    setTimeout(() => this.responses.next(answer), 1000);
  }
  protected baseUrl = this.applicationConfigService.getEndpointFor('api/chat');

  constructor(private http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  public submit(question: string): void {
    //console.log(data)
    this.http
      .post<Mess>(this.baseUrl, question)
      .toPromise()
      .then(res => {
        console.log(res);
        this.responses.next(res.contenu);
      });
  }

  /*get(): Observable<string> {
    console.log(this.http.get<string>(this.baseUrl))
    return this.http.get<string>(this.baseUrl);
  }*/

  /*getAll(): Observable<Evenement[]> {
    return this.http.get<Evenement[]>(baseUrl);
  }*/
}
