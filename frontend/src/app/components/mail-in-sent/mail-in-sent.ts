import { Component, inject, input, signal, WritableSignal } from '@angular/core';
import { AuthenticationService } from '../../services/authentication.service';
import { MailService } from '../../services/mail-service';
import { SentMail } from '../../types/mail';
import { AttachmentComponent } from '../attachment/attachment.component';

@Component({
  selector: 'app-mail-in-sent',
  imports: [AttachmentComponent],
  templateUrl: './mail-in-sent.html',
  styleUrl: './mail-in-sent.css',
})
export class MailInSent {
  mailService = inject(MailService);
  authService: undefined | AuthenticationService;
  mail = signal<SentMail | null>(null);
  senderInitials() {
    return this.mail()
      ?.data.sender?.name.split(' ')
      .slice(0, 2)
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }
  constructor(authService: AuthenticationService) {
    this.authService = authService;
  }

  ngOnInit(): void {
    this.mailService.getSentEmail(this.currentMailId()() as number).subscribe({
      next: (d: any) => {
        console.log(d);
        this.mail.set(d);
      },
    });
  }

  currentMailId = input.required<WritableSignal<number | null>>();
  handleBackToSent = () => {
    this.currentMailId().set(null);
    this.mail.set(null);
  };

  onDelete() {
    this.mailService.deleteMail(this.currentMailId()() as number).subscribe({
      next: (value) => {
        console.log(value);
      },
    });
    this.currentMailId().set(null);
  }

  onMoveToFolder() {
    // TODO: Implement move to folder functionality
  }
}
