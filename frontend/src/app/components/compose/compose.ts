import {
  Component,
  EventEmitter,
  Inject,
  Input,
  Output,
  computed,
  inject,
  signal,
} from '@angular/core';
import { MailService } from '../../services/mail-service';
import { AuthenticationService } from '../../services/authentication.service';
import { FormsModule } from '@angular/forms';
import { AttachmentService } from '../../services/attachment.service';

@Component({
  selector: 'app-compose',
  imports: [FormsModule],
  templateUrl: './compose.html',
  styleUrl: './compose.css',
})
export class Compose {
  private mailService = inject(MailService);
  private authService = inject(AuthenticationService);
  private attachmentService = Inject(AttachmentService);

  @Output() closeCompose = new EventEmitter<void>();
  @Input() isComposeOpen: boolean = false;

  fromUserId = computed(() => this.authService.user()?.id || 0);
  toEmails = signal<string[]>([]);
  toEmailInput = signal('');
  subject = signal('');
  body = signal('');
  priority = signal('NORMAL');
  attachments = signal<File[]>([]);

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
    this.attachments.set([]);
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

  attachmentName(attachment: File) {
    return attachment.name;
  }

  attachmentSize(attachment: File) {
    const units = ['B', 'K', 'M', 'G', 'T'];

    let size = attachment.size;
    let unit = 0;

    while (size >= 1024 && unit < units.length - 1) {
      size = size / 1024;
      unit++;
    }

    return `${Math.round(size)}${units[unit]}`;
  }

  addAttachments(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) {
      return;
    }

    this.attachments.set([...this.attachments(), ...Array.from(input.files)]);
  }

  removeAttachment(index: number) {
    this.attachments.set(this.attachments().filter((_, i) => i !== index));
  }
}
