import { Component } from '@angular/core';
import { Message, SendMessageEvent, User } from '@progress/kendo-angular-conversational-ui';
import { ChatService } from './evenement.service';
import { Mess } from './message';
import { async, from, merge, Observable, Subject } from 'rxjs';
import { map, scan } from 'rxjs/operators';

@Component({
  selector: 'jhi-chat-component',
  styleUrls: ['../../../../../node_modules/bootstrap/dist/css/bootstrap.min.css'],
  template: ` <kendo-chat [messages]="feed | async" [user]="user" (sendMessage)="sendMessage($event)"></kendo-chat> `,
})
export class ChatComponent {
  constructor(private svc: ChatService) {}
  public readonly user: User = {
    id: 1,
  };

  public readonly bot: User = {
    id: 0,
  };
  hello: Message = {
    text: 'Hello!',
    author: this.bot,
  };
  private local: Subject<Message> = new Subject<Message>();
  // Merge local and remote messages into a single stream
  public feed: Observable<Message[]> = merge(
    from([this.hello]),
    this.local,
    this.svc.responses.pipe(
      map(
        (response: string): Message => ({
          author: this.bot,
          text: response,
        })
      )
    )
  ).pipe(
    // ... and emit an array of all messages
    scan((acc: Message[], x: Message) => [...acc, x], [])
  );

  public sendMessage(e: SendMessageEvent): void {
    this.local.next(e.message);

    this.local.next({
      author: this.bot,
      typing: true,
    });
    this.feed.pipe();
    this.svc.submit(e.message.text != undefined ? e.message.text : '');
  }
}
