import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { AuthenticationService } from '../../services/authentication.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-compose',
  imports: [FormsModule],
  templateUrl: './compose.html',
  styleUrl: './compose.css',
})
export class Compose {
  private mailService = inject(MailService);
  private authService = inject(AuthenticationService);

  ngOnInit(): void {
    this.authService.loadUser().subscribe((user) => {
      if (user) this.fromUserId.set(user.id);
      console.log('From User ID in Compose:', this.fromUserId());
      console.log('User in Compose:', user);
    });
  }

  @Output() closeCompose = new EventEmitter<void>();
  @Input() isComposeOpen: boolean = false;

  fromUserId = signal(0);
  toEmails = signal<string[]>([]);
  toEmailInput = signal('');
  subject = signal('');
  body = signal('');
  priority = signal('NORMAL');

  // ! Close the compose window
  public closeWindow(): void {
    this.closeCompose.emit();
  }

  sendEmail() {
    // console.log('Sending email from user ID:', this.fromUserId());
    // console.log('To Emails:', this.toEmails());
    // console.log('Subject:', this.subject());
    // console.log('Body:', this.body());
    // console.log('Priority:', this.priority());
    this.mailService
      .sendEmail(this.fromUserId(), this.toEmails(), this.subject(), this.body(), this.priority())
      .subscribe({
        // Handle the successful response (the string "sent" from the backend)
        next: (response: any) => {
          // Print the returned string to the console
          console.log('✅ API Response (Email Sent):', response);

          // Perform success actions ONLY after successful send
          this.clearForm();
          // this.closeWindow();
        },

        // Handle any errors during the API call (e.g., recipient not found, 4xx/5xx errors)
        error: (error) => {
          console.error('❌ Email Sending Failed:', error);
          // Optional: Display an error message to the user
        },

        // Optional: Runs when the observable completes (after next/error)
        complete: () => {
          console.log('📧 Send email Observable complete.');
        },
      });
    this.clearForm();
    this.closeWindow();
  }

  clearForm() {
    this.toEmails.set([]);
    this.subject.set('');
    this.body.set('');
    this.priority.set('NORMAL');
  }

  addEmail() {
    if (this.toEmailInput().trim()) {
      this.toEmails.set([...this.toEmails(), this.toEmailInput().trim()]);
      this.toEmailInput.set('');
    }
  }

  removeEmail(i: number) {
    this.toEmails.set(this.toEmails().filter((_, index) => index !== i));
  }
}
