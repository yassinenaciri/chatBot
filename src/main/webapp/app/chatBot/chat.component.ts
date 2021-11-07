import { Component } from '@angular/core';
import { Message, SendMessageEvent, User } from '@progress/kendo-angular-conversational-ui';
import { EvenementService } from './evenement.service';
import { Mess } from './message';

const bot: User = {
  id: 0,
};

const user: User = {
  id: 1,
};

const hello: Message = {
  text: 'Hello!',
  author: bot,
};

@Component({
  selector: 'jhi-chat-component',
  styleUrls: ['../../../../../node_modules/bootstrap/dist/css/bootstrap.min.css'],
  template: ` <kendo-chat [messages]="feed" [user]="user" (sendMessage)="sendMessage($event)"></kendo-chat> `,
})
export class ChatComponent {
  public feed: Message[] = [hello];
  public readonly user: User = user;
  public readonly bot: User = bot;
  currentIndex = -1;
  title = '';
  contenu!: string;

  constructor(private evenementService: EvenementService) {}

  /*ngOnInit(): void {
    this.retrieveEvenements();
  }*/

  /*retrieveEvenements(): void {
    this.evenementService.getAll()
      .subscribe(
        data => {
          this.evenements = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }//

  setActiveEvenement(evenement: Evenement, index: number): void {
    this.currentEvent = evenement;
    this.currentIndex = index;
  }*/

  public sendMessage(e: SendMessageEvent): void {
    //this.evenementService.create(`${e.message.text}`)

    //console.log(e.message.text)
    this.evenementService.create(new Mess(e.message.text)).subscribe(data => {
      //console.log(typeof data.contenu)
      this.contenu = data.contenu!;
    });
    //this.mess.contenu=e.message.text!;
    //console.log(this.mess.contenu)

    const echo: Message = {
      author: bot,
      text: `"${this.contenu}"`,
    };

    this.feed = [...this.feed, e.message, echo];
  }
}
