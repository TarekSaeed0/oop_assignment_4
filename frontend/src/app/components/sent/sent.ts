import { Component, effect, inject, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { MailResponce } from '../../types/mail';
import { MailInbox } from "../mail-inbox/mail-inbox";
import { AuthenticationService } from '../../services/authentication.service';

@Component({
  selector: 'app-sent',
  imports: [MailInbox],
  templateUrl: './sent.html',
  styleUrl: './sent.css',
})
export class Sent {
  mailSerivce = inject(MailService)
  authService: undefined | AuthenticationService = undefined;
  constructor(authSevice: AuthenticationService) {
    this.authService = authSevice
    effect(() => {
      if (this.currentMailId() == null) {
        this.mailSerivce.getSent(2, this.page(), this.size(), "", "", false, "any").subscribe({
          next: (d: any) => {
            console.log(d);
            this.sent.set(d);
          }
        })
      }
    })
  }

  sent = signal<MailResponce>([])
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
      console.log(id);

      if (l.includes(id)) return l.filter((d) => d != id)
      else {
        l.push(id)
        return l
      }
    })
  }


}
