import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Ensure this is imported
import { MailService } from '../../services/mail-service';
import { AuthenticationService } from '../../services/authentication.service';
import { MailInbox } from "../mail-inbox/mail-inbox";
import { UserFolderService } from '../../services/UserFolderService';
import { MailInSent } from "../mail-in-sent/mail-in-sent";

@Component({
  selector: 'app-custom-folder',
  standalone: true,
  imports: [CommonModule, MailInbox, DatePipe, SlicePipe, MailInSent, FormsModule],
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
  allMails = signal<any[]>([]); 
  
  // --- Filter State ---
  searchQuery = signal<string>('');
  filterType = signal<'General' | 'Sender' | 'Subject' | 'Body' | 'Priority'>('General');
  
  // Computed: Filters based on Query AND selected Filter Type
  filteredMails = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const type = this.filterType();
    const mails = this.allMails();

    if (!query) return mails;

    return mails.filter(mail => {
      const sender = (mail.data.senderName || '') + ' ' + (mail.data.senderEmail || '');
      const subject = mail.data.subject || '';
      const body = mail.data.body || '';
      const priority = mail.data.priority || '';

      if (type === 'General') {
        // Search EVERYWHERE
        return sender.toLowerCase().includes(query) ||
               subject.toLowerCase().includes(query) ||
               body.toLowerCase().includes(query) ||
               priority.toLowerCase().includes(query);
      } 
      else if (type === 'Sender') {
        return sender.toLowerCase().includes(query);
      }
      else if (type === 'Subject') {
        return subject.toLowerCase().includes(query);
      }
      else if (type === 'Body') {
        return body.toLowerCase().includes(query);
      }
      else if (type === 'Priority') {
        return priority.toLowerCase().includes(query);
      }
      return false;
    });
  });

  // State for View
  displayedMails = signal<any[]>([]); 
  currentMailId = signal<null | number>(null);
  selectedMail = signal<number[]>([]);

  isCurrentMailSentByMe = computed(() => {
    const selectedId = this.currentMailId();
    const currentUserEmail = this.authService.user()?.email;
    if (!selectedId || !currentUserEmail) return false;
    const mail = this.displayedMails().find(m => m.id === selectedId);
    return mail?.data?.senderEmail === currentUserEmail;
  });

  // Pagination
  page = signal<number>(1);
  size = signal<number>(10);

  constructor() {
    // Reload mails when folder name changes
    effect(() => {
      this.page.set(1); 
      this.selectedMail.set([]); 
      this.currentMailId.set(null); 
      this.searchQuery.set(''); 
      this.filterType.set('General'); // Reset filter type
      this.handleRefresh();
    });

    // Update displayed mails whenever the filtered list or page changes
    effect(() => {
      this.updateDisplayedMails();
    });
  }

  handleRefresh = () => {
    const userId = this.authService.user()?.id;
    const name = this.folderName();

    if (userId && name) {
      this.mailService.getMailsByFolder(userId, name).subscribe({
        next: (data) => {
          this.allMails.set(data);
        },
        error: (err) => console.error(err)
      });
    }
  }

  updateDisplayedMails() {
    const source = this.filteredMails(); 
    const startIndex = (this.page() - 1) * this.size();
    const endIndex = startIndex + this.size();
    this.displayedMails.set(source.slice(startIndex, endIndex));
  }

  // Handle Search Input Change
  handleSearchChange(newValue: string) {
    this.searchQuery.set(newValue);
    this.page.set(1); 
  }

  // Handle Dropdown Change
  handleFilterTypeChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    this.filterType.set(selectElement.value as any);
    this.page.set(1);
  }

  handleNextPage = () => {
    if (this.hasNextPage()) {
      this.page.update((p) => p + 1);
    }
  }

  handlePrevPage = () => {
    if (this.hasPrevPage()) {
      this.page.update((p) => p - 1);
    }
  }

  hasPrevPage() {
    return this.page() > 1;
  }

  hasNextPage() {
    return (this.page() * this.size()) < this.filteredMails().length;
  }

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
    return (this.displayedMails().length > 0) && 
           this.displayedMails().every(m => this.selectedMail().includes(m.id));
  }

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