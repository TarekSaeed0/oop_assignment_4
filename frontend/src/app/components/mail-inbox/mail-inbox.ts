import { Component, inject, input, OnInit, signal, WritableSignal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { Mail } from '../../types/mail';

@Component({
  selector: 'app-mail-inbox',
  imports: [],
  templateUrl: './mail-inbox.html',
  styleUrl: './mail-inbox.css',
})
export class MailInbox implements OnInit {
  mailService = inject(MailService);
  ngOnInit(): void {
    this.mailService.getEmail(this.currentMailId()() as number).subscribe({
      next: (d: any) => {
        console.log(d);
        this.mail.set(d);
      }
    })
  }

  constructor() {

  }
  currentMailId = input.required<WritableSignal<number | null>>()
  mail = signal<Mail | null>(null)
  handleBackToInbox = () => {
    this.currentMailId().set(null)
    this.mail.set(null);
  }
}
