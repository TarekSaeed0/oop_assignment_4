import { Component, EventEmitter, Input, Output, WritableSignal, computed, inject, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { AuthenticationService } from '../../services/authentication.service';
import { FormsModule } from '@angular/forms';
import { AttachmentService } from '../../services/attachment.service';
import { Attachment } from '../../types/mail';
import { AttachmentComponent } from '../attachment/attachment.component';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-compose',
  imports: [FormsModule, AttachmentComponent],
  templateUrl: './compose.html',
  styleUrl: './compose.css',
})
export class Compose {
  private mailService = inject(MailService);
  private authService = inject(AuthenticationService);
  private attachmentService = inject(AttachmentService);

  @Output() closeCompose = new EventEmitter<void>();
  @Input() isComposeOpen: boolean = false;

fromUserId: WritableSignal<number> = signal(0);  toEmails = signal<string[]>([]);
  toEmailInput = signal('');
  subject = signal('');
  body = signal('');
  priority = signal('NORMAL');
  attachments = signal<Attachment[]>([]);

  isUploadingAttachments = false;
  error = null;
  
  // Holds the error message if a user is not found
  userNotFound = signal(null as string | null);
  
  // Loading state to prevent double clicks
  isSending = signal(false);

  ngOnInit(): void {
    this.authService.loadUser().subscribe((user) => {
      if (user) this.fromUserId.set(user.id);
    });
  }

  public closeWindow(): void {
    this.closeCompose.emit();
  }

  isitme() {
    return this.toEmails().includes(this.authService.user()?.email || '');
  }

  // ! REFACTORED SEND LOGIC
  sendEmail() {
    const recipients = this.toEmails().filter((l) => this.authService.user()?.email != l);

    if (recipients.length === 0) {
      console.error('❌ No valid recipient email addresses provided.');
      return;
    }

    this.userNotFound.set(null);
    this.isSending.set(true);

    this.mailService
      .sendEmail(
        this.fromUserId(),
        this.toEmails(),
        this.subject(),
        this.body(),
        this.priority(),
        this.attachments(),
      )
      this.mailService.isValidEmail(this.fromUserId(), recipients, this.subject(), this.body(), this.priority())
      .subscribe({
        next: (checkResponse) => {
          console.log('✅ Validation Passed:', checkResponse);

          this.performSend(recipients);
        },
        error: (error: HttpErrorResponse) => {
          this.handleError(error);
          this.isSending.set(false);
        },
      });
  }

 // src/app/components/compose/compose.ts

  private performSend(recipients: string[]) {
    this.mailService
      .sendEmail(
        this.fromUserId(),
        recipients,
        this.subject(),
        this.body(),
        this.priority(),
        this.attachments() // <--- ADD THIS (The 6th argument)
      )
      .subscribe({
        next: (response) => {
          this.clearForm();
          this.isSending.set(false);
          this.closeWindow();
        },
        error: (error: HttpErrorResponse) => {
          console.error('❌ Send Failed:', error);
          this.isSending.set(false);
        },
      });
  }

  // Helper to handle the backend error
  private handleError(error: HttpErrorResponse) {
    console.error('❌ Validation Failed:', error);
    
    try {
      const errorBody = typeof error.error === 'string' ? JSON.parse(error.error) : error.error;
      
      // Extract the message (which contains the email)
      const invalidEmail = errorBody.message; 
      
      this.userNotFound.set(invalidEmail);
    } catch (e) {
      this.userNotFound.set('Unknown error occurred');
    }
  }

  clearForm() {
    this.toEmails.set([]);
    this.subject.set('');
    this.body.set('');
    this.priority.set('NORMAL');
    this.attachments.set([]);
    this.userNotFound.set(null);
  }

  addEmail() {
    if (this.toEmailInput().trim()) {
      this.toEmails.set([...this.toEmails(), this.toEmailInput().trim()]);
      this.toEmailInput.set('');
      this.userNotFound.set(null);
    }
  }

  removeEmail(i: number) {
    this.toEmails.set(this.toEmails().filter((_, index) => index !== i));
    this.userNotFound.set(null);
  }

  addAttachments(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) {
      return;
    }

    this.error = null;
    this.isUploadingAttachments = true;

    this.attachmentService.uploadAttachments(Array.from(input.files)).subscribe({
      next: (attachments) => {
        this.attachments.set([...this.attachments(), ...attachments]);
        this.isUploadingAttachments = false;
      },
      error: (error) => {
        this.error = error.error.message || 'Failed to upload attachments';
        this.isUploadingAttachments = false;
      },
    });
  }

  removeAttachment(attachment: Attachment) {
    this.attachments.set(this.attachments().filter((a) => a.id !== attachment.id));
  }
}
