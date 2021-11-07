import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { Message, SendMessageEvent, User } from '@progress/kendo-angular-conversational-ui';

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
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;

  public feed: Message[] = [hello];
  public readonly user: User = user;
  public readonly bot: User = bot;
  private readonly destroy$ = new Subject<void>();
  constructor(private accountService: AccountService, private router: Router) {}
  public sendMessage(e: SendMessageEvent): void {
    const echo: Message = {
      author: bot,
      text: `You said: `,
    };

    this.feed = [...this.feed, e.message, echo];
  }
  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
