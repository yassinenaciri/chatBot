import { Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Mess } from './message';
import { ApplicationConfigService } from '../core/config/application-config.service';

// Mock remote service

@Injectable()
export class ChatService {
  public readonly responses: Subject<string> = new Subject<string>();

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
}
