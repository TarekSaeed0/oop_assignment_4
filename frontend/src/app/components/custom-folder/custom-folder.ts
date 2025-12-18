import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
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

  // --- State Signals ---
  allMails = signal<any[]>([]); 
  currentMailId = signal<null | number>(null);
  selectedMail = signal<number[]>([]);

  // Search & Filter State
  searchQuery = signal<string>('');
  filterType = signal<'General' | 'Sender' | 'Subject' | 'Body' | 'Priority'>('General');
  
  // Sort State
  sortKey = signal<'date' | 'sender' | 'subject'>('date');
  sortOrder = signal<'asc' | 'desc'>('desc');

  // Pagination State
  page = signal<number>(1);
  size = signal<number>(10);

  // --- Reactive Pipeline (Filter -> Sort) ---
  filteredAndSortedMails = computed(() => {
    let list = [...this.allMails()];
    const query = this.searchQuery().toLowerCase().trim();
    const type = this.filterType();

    // 1. Filtering Logic
    if (query) {
      list = list.filter(mail => {
        const sender = `${mail.data.senderName || ''} ${mail.data.senderEmail || ''}`.toLowerCase();
        const subject = (mail.data.subject || '').toLowerCase();
        const body = (mail.data.body || '').toLowerCase();
        const priority = (mail.data.priority || '').toLowerCase();

        switch (type) {
          case 'Sender': return sender.includes(query);
          case 'Subject': return subject.includes(query);
          case 'Body': return body.includes(query);
          case 'Priority': return priority.includes(query);
          default: 
            return sender.includes(query) || subject.includes(query) || 
                   body.includes(query) || priority.includes(query);
        }
      });
    }

    // 2. Sorting Logic
    const key = this.sortKey();
    const order = this.sortOrder();

    list.sort((a, b) => {
      let valA: any, valB: any;
      if (key === 'date') {
        valA = new Date(a.data.sentAt).getTime();
        valB = new Date(b.data.sentAt).getTime();
      } else if (key === 'sender') {
        valA = (a.data.senderName || '').toLowerCase();
        valB = (b.data.senderName || '').toLowerCase();
      } else {
        valA = (a.data.subject || '').toLowerCase();
        valB = (b.data.subject || '').toLowerCase();
      }

      if (valA < valB) return order === 'asc' ? -1 : 1;
      if (valA > valB) return order === 'asc' ? 1 : -1;
      return 0;
    });

    return list;
  });

  // 3. Reactive Pagination Slice
  displayedMails = computed(() => {
    const start = (this.page() - 1) * this.size();
    return this.filteredAndSortedMails().slice(start, start + this.size());
  });

  // --- Computed Helpers ---
  totalPages = computed(() => Math.ceil(this.filteredAndSortedMails().length / this.size()) || 1);
  hasPrevPage = computed(() => this.page() > 1);
  hasNextPage = computed(() => (this.page() * this.size()) < this.filteredAndSortedMails().length);

  isCurrentMailSentByMe = computed(() => {
    const selectedId = this.currentMailId();
    const currentUserEmail = this.authService.user()?.email;
    if (!selectedId || !currentUserEmail) return false;
    const mail = this.allMails().find(m => m.id === selectedId);
    return mail?.data?.senderEmail === currentUserEmail;
  });

  constructor() {
    // Reset view and reload when folder changes
    effect(() => {
      this.folderName(); 
      this.resetView();
      this.handleRefresh();
    }, { allowSignalWrites: true });
  }

  // --- Handlers ---
  resetView() {
    this.page.set(1);
    this.selectedMail.set([]);
    this.currentMailId.set(null);
    this.searchQuery.set('');
    this.sortKey.set('date');
    this.sortOrder.set('desc');
  }

  handleRefresh = () => {
    const userId = this.authService.user()?.id;
    const name = this.folderName();
    if (userId && name) {
      this.mailService.getMailsByFolder(userId, name).subscribe({
        next: (data) => this.allMails.set(data),
        error: (err) => console.error(err)
      });
    }
  }

  handleSearchChange(newValue: string) {
    this.searchQuery.set(newValue);
    this.page.set(1); 
  }

  handleFilterTypeChange(event: Event) {
    this.filterType.set((event.target as HTMLSelectElement).value as any);
    this.page.set(1);
  }

  toggleSort(key: 'date' | 'sender' | 'subject') {
    if (this.sortKey() === key) {
      this.sortOrder.update(o => o === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortKey.set(key);
      this.sortOrder.set(key === 'date' ? 'desc' : 'asc');
    }
  }

  handleNextPage = () => { if (this.hasNextPage()) this.page.update(p => p + 1); }
  handlePrevPage = () => { if (this.hasPrevPage()) this.page.update(p => p - 1); }

  handleSelectMail = (id: number) => {
    this.selectedMail.update(l => l.includes(id) ? l.filter(d => d !== id) : [...l, id]);
  }

  handleSelectAll = () => {
    if (this.isAllSelected()) this.selectedMail.set([]);
    else this.selectedMail.set(this.displayedMails().map(e => e.id));
  }

  isAllSelected() {
    return this.displayedMails().length > 0 && 
           this.displayedMails().every(m => this.selectedMail().includes(m.id));
  }

  handleClickEmail(id: number) { this.currentMailId.set(id); }

  handleDelete(id: number) {
    this.mailService.deleteMail(id).subscribe({ next: () => this.handleRefresh() });
  }

  handleBulkDelete() {
    this.mailService.bulkDelete(this.selectedMail()).subscribe({
      next: () => {
        this.handleRefresh();
        this.selectedMail.set([]);
      }
    });
  }

  handleRemoveFromFolder() {
    const userId = this.authService.user()?.id;
    if (userId && this.selectedMail().length > 0) {
      this.userFolderService.deleteFromFolder(userId, this.folderName(), this.selectedMail()).subscribe({
        next: () => {
          this.handleRefresh();
          this.selectedMail.set([]);
        }
      });
    }
  }
}