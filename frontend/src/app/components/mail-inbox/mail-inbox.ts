import { Component, inject, input, OnInit, signal, WritableSignal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { InboxMail } from '../../types/mail';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../services/authentication.service';
import { AttachmentComponent } from '../attachment/attachment.component';

@Component({
  selector: 'app-mail-inbox',
  imports: [AttachmentComponent],
  templateUrl: './mail-inbox.html',
  styleUrl: './mail-inbox.css',
})
export class MailInbox implements OnInit {
  mailService = inject(MailService);
  authService: undefined | AuthenticationService;
  constructor(authService: AuthenticationService) {
    this.authService = authService;
  }
  ngOnInit(): void {
    this.mailService.getInboxEmail(this.currentMailId()() as number).subscribe({
      next: (d: any) => {
        console.log(d);
        this.mail.set(d);
      },
    });
  }

  currentMailId = input.required<WritableSignal<number | null>>();
  mail = signal<InboxMail | null>(null);
  senderInitials() {
    return this.mail()
      ?.data.sender?.name.split(' ')
      .slice(0, 2)
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }
  handleBackToInbox = () => {
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
