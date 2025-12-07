import { Mail } from './../../types/mail';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { MailResponce } from '../../types/mail';
import { MailInbox } from "../mail-inbox/mail-inbox";
import { AuthenticationService } from '../../services/authentication.service';

@Component({
  selector: 'app-inbox',
  imports: [MailInbox],
  templateUrl: './inbox.html',
  styleUrl: './inbox.css',
})
export class Inbox implements OnInit {
  mailSerivce = inject(MailService)
  authService: undefined | AuthenticationService = undefined;
  constructor(authSevice: AuthenticationService) {
    this.authService = authSevice
    effect(() => {
      if (this.currentMailId() == null) {
        this.mailSerivce.getInbox(authSevice.user()?.id || 0, this.page(), this.size(), "", "", false, "any").subscribe({
          next: (d: any) => {
            console.log(d);
            this.inbox.set(d);
          }
        })
      }
    })
  }
  ngOnInit(): void {

  }
  inbox = signal<MailResponce>([])
  selectMail(id: number) {
    this.currentMailId.set(id)
  }
  currentMailId = signal<null | number>(null);
  page = signal<number>(1);
  size = signal<number>(10);
  handleClickEmail(id: number) {
    this.currentMailId.set(id)
  }
  selectedMail = signal<number[]>([])
  handleSelectMail = (id: number) => {
    this.selectedMail.update((l) => {
      if (l.includes(id)) return l.filter((d) => d != id)
      else {
        l.push(id)
        return l
      }
    })
  }

}
