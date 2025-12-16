import { Component, EventEmitter, Input, Output, computed, inject, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { AuthenticationService } from '../../services/authentication.service';
import { FormsModule } from '@angular/forms';
import { AttachmentService } from '../../services/attachment.service';
import {Attachment, Draft} from '../../types/mail';
import { AttachmentComponent } from '../attachment/attachment.component';
import { HttpErrorResponse } from '@angular/common/http';
import { switchMap } from 'rxjs/operators';
import {DraftsService} from '../../services/draftsService/drafts-service';

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
  private draftService = inject(DraftsService)
  @Output() closeCompose = new EventEmitter<void>();
  @Input() isComposeOpen: boolean = false;

  toEmails = signal<string[]>([]);
  toEmailInput = signal('');
  subject = signal('');
  body = signal('');
  priority = signal('NORMAL');
  attachments = signal<Attachment[]>([]);
  draftId = signal<number | null>(null)
  isUploadingAttachments = false;
  error = null;

  // Holds the error message if a user is not found
  userNotFound = signal(null as string | null);

  // Loading state to prevent double clicks
  isSending = signal(false);

  ngOnInit(): void {
    this.draftService.putDraft$.subscribe((draftId) => {
      this.draftId.set(draftId)
      this.draftService.getDraft(draftId)
        .subscribe({
          next: (data) => {
            const draftData = data as Draft
            this.subject.set(draftData.subject)
            this.priority.set(draftData.priority)
            this.body.set(draftData.body)
            this.toEmails.set(draftData.receivers.map((rec) => rec.email))
            this.attachments.set(draftData.attachments)
            this.isComposeOpen = true
          }
        })
    })
  }
  handleSaveDraft() {
    if(this.draftId() == null) {
      this.mailService.isValidEmail(this.authService.user()?.id || 0, this.toEmails(), this.subject(), this.body(), this.priority())
        .pipe(switchMap(() =>
          this.draftService.createDraft(
            this.body(),
            this.subject(),
            this.toEmails().map(email => {return {email}}),
            this.priority(),
            this.attachments()
          )))
      .subscribe({
        next: (res: any) => {
          console.log("draft created", res)
          this.draftId.set(res.id)
        }
      })
    } else {
      const tempId = this.draftId()
      const tempBody = this.body()
      const tempSubject =   this.subject()
      const tempTo =   this.toEmails().map(email => {return {email}})
      const tempP =   this.priority()
      const tempAtt =   this.attachments()
      this.mailService
        .isValidEmail(this.authService.user()?.id || 0, this.toEmails(), this.subject(), this.body(), this.priority())
        .pipe(
          switchMap(() =>
            this.draftService.updateDraft(
              tempId as number,
              tempBody,
              tempSubject,
              tempTo,
              tempP,
              tempAtt
            )
          )
        )
        .subscribe({
          next: (response: any) => {
            console.log('✅ draft updated:', response);
          },
          error: (error) => {
            console.error('❌ draft Failed:', error);
          },
        });
    }
  }

  public closeWindow(): void {
    if(this.body() || this.subject() || this.toEmails().length!=0){
      this.handleSaveDraft()
    }
    this.body.set("")
    this.subject.set("")
    this.toEmails.set([])
    this.draftId.set(null)
    this.isComposeOpen = false
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
      .isValidEmail(this.authService.user()?.id || 0, recipients, this.subject(), this.body(), this.priority())
      .pipe(
        switchMap(() =>
          this.mailService.sendEmail(
            this.authService.user()?.id || 0,
            this.toEmails(),
            this.subject(),
            this.body(),
            this.priority(),
            this.attachments(),
          )
        )
      )
      .subscribe({
        next: (response: any) => {
          console.log('✅ Email Sent:', response);

          this.clearForm();
          this.isSending.set(false);
          this.closeWindow();
        },
        error: (error) => {
          console.error('❌ Send Failed:', error);
          this.isSending.set(false);
        },
      });
  }

  // Helper to handle the backend error
  private handleError(error: any) {
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
