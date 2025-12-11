import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { MailService } from '../../services/mail-service';
import { AuthenticationService } from '../../services/authentication.service';
import { MailInbox } from "../mail-inbox/mail-inbox";
import { UserFolderService } from '../../services/UserFolderService';
import { MailInSent } from "../mail-in-sent/mail-in-sent";

@Component({
  selector: 'app-custom-folder',
  standalone: true,
  imports: [CommonModule, MailInbox, DatePipe, SlicePipe, MailInSent],
  templateUrl: './custom-folder.html',
  styleUrl: './custom-folder.css'
})
export class CustomFolderComponent {
  // Inputs
  folderName = input.required<string>();

  // Dependencies
  private mailService = inject(MailService);
  private authService = inject(AuthenticationService);
  private userFolderService = inject(UserFolderService);
  // State
  allMails = signal<any[]>([]); // Stores all fetched mails
  displayedMails = signal<any[]>([]); // Stores current page
  currentMailId = signal<null | number>(null);
  selectedMail = signal<number[]>([]);

  isCurrentMailSentByMe = computed(() => {
    const selectedId = this.currentMailId();
    console.log("Selected Mail ID:", selectedId);
    const currentUserEmail = this.authService.user()?.email;

    if (!selectedId || !currentUserEmail) return false;

    const mail = this.displayedMails().find(m => m.id === selectedId);
    console.log("Mail", mail);

    console.log("Mail sende", mail?.data?.senderEmail);
    console.log("Current user :", currentUserEmail);
    return mail?.data?.senderEmail === currentUserEmail; 
  });


  // Pagination
  page = signal<number>(1);
  size = signal<number>(10);

  constructor() {
    // Reload mails when folder name changes
    effect(() => {
      this.page.set(1); // Reset to page 1
      this.selectedMail.set([]); // Clear selection
      this.currentMailId.set(null); // Close open email
      this.handleRefresh();
    });
  }

  handleRefresh = () => {
    const userId = this.authService.user()?.id;
    const name = this.folderName();

    if (userId && name) {
      this.mailService.getMailsByFolder(userId, name).subscribe({
        next: (data) => {
          this.allMails.set(data);
          this.updateDisplayedMails();
        },
        error: (err) => console.error(err)
      });
    }
  }

  // --- Client Side Pagination Logic ---
  updateDisplayedMails() {
    const startIndex = (this.page() - 1) * this.size();
    const endIndex = startIndex + this.size();
    this.displayedMails.set(this.allMails().slice(startIndex, endIndex));
  }

  handleNextPage = () => {
    if (this.hasNextPage()) {
      this.page.update((p) => p + 1);
      this.updateDisplayedMails();
    }
  }

  handlePrevPage = () => {
    if (this.hasPrevPage()) {
      this.page.update((p) => p - 1);
      this.updateDisplayedMails();
    }
  }

  hasPrevPage() {
    return this.page() > 1;
  }

  hasNextPage() {
    return (this.page() * this.size()) < this.allMails().length;
  }

  // --- Selection Logic ---
  handleSelectMail = (id: number) => {
    this.selectedMail.update((l) => {
      if (l.includes(id)) return l.filter((d) => d != id);
      else {
        l.push(id);
        return l;
      }
    });
  }

  handleSelectAll = () => {
    if (this.isAllSelected()) {
      this.selectedMail.set([]);
    } else {
      this.selectedMail.set(this.displayedMails().map((e) => e.id));
    }
  }

  isAllSelected() {
    return (this.selectedMail().length === this.displayedMails().length) && (this.displayedMails().length !== 0);
  }

  // --- Actions ---
  handleClickEmail(id: number) {
    this.currentMailId.set(id);
  }

  handleDelete(id: number) {
    this.mailService.deleteMail(id).subscribe({
      next: () => this.handleRefresh()
    });
  }

  handleBulkDelete() {
    const mailList = this.selectedMail();
    this.mailService.bulkDelete(mailList).subscribe({
      next: () => {
        this.handleRefresh();
        this.selectedMail.set([]);
      }
    });
  }
  
  // Placeholder for move logic
  handleBulkMove() {
    console.log("Move logic implementation needed");
  }

  handleRemoveFromFolder() {
    const userId = this.authService.user()?.id;
    const folderName = this.folderName();
    const mailIds = this.selectedMail();
    if (userId && folderName && mailIds.length > 0) {
      this.userFolderService.deleteFromFolder(userId, folderName, mailIds).subscribe({
        next: () => {
          this.handleRefresh();
          this.selectedMail.set([]);
        }
      });
    }
  }
  
}