
import { Component, inject, Input, OnChanges, OnInit, signal, computed } from '@angular/core';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrashService } from '../../services/trash-service'; 
import { DeletedMailDTO, MailSource } from '../../types/trash';
import { AuthenticationService } from '../../services/authentication.service';
import { MailInTrashComponent } from '../mail-in-trash/mail-in-trash';

@Component({
  selector: 'app-trashfolder',
  standalone: true,
  imports: [DatePipe, MailInTrashComponent, CommonModule, FormsModule],
  templateUrl: './trashfolder.html', 
  styleUrl: './trashfolder.css'
})
export class TrashfolderComponent implements OnInit, OnChanges {
  private trashService = inject(TrashService);
  private authService = inject(AuthenticationService);

  // --- State Signals ---
  allMails = signal<DeletedMailDTO[]>([]);
  selectedMail = signal<DeletedMailDTO | null>(null);
  selectedIds = signal<number[]>([]); 
  
  // Search & Filter
  searchQuery = signal<string>('');
  filterType = signal<'General' | 'Sender' | 'Subject' | 'Body'>('General');
  
  // Sort
  sortKey = signal<'date' | 'sender' | 'subject'>('date');
  sortOrder = signal<'asc' | 'desc'>('desc');

  // Pagination
  page = signal<number>(1);
  size = signal<number>(10);

  @Input() searchBy = ""; 

  // --- Computed Pipeline (Filter -> Sort) ---
  filteredAndSortedMails = computed(() => {
    let list = [...this.allMails()];
    const query = this.searchQuery().toLowerCase().trim();
    const type = this.filterType();

    // 1. Filter
    if (query) {
      list = list.filter(mail => {
        const sender = (mail.sender?.name || '').toLowerCase();
        const subject = (mail.subject || '').toLowerCase();
        const body = (mail.body || '').toLowerCase();
        
        switch(type) {
          case 'Sender': return sender.includes(query);
          case 'Subject': return subject.includes(query);
          case 'Body': return body.includes(query);
          default: return sender.includes(query) || subject.includes(query) || body.includes(query);
        }
      });
    }

    // 2. Sort
    const key = this.sortKey();
    const order = this.sortOrder();

    list.sort((a, b) => {
      let valA: any, valB: any;
      if (key === 'date') {
        valA = new Date(a.date).getTime();
        valB = new Date(b.date).getTime();
      } else if (key === 'sender') {
        valA = (a.sender?.name || '').toLowerCase();
        valB = (b.sender?.name || '').toLowerCase();
      } else {
        valA = (a.subject || '').toLowerCase();
        valB = (b.subject || '').toLowerCase();
      }

      if (valA < valB) return order === 'asc' ? -1 : 1;
      if (valA > valB) return order === 'asc' ? 1 : -1;
      return 0;
    });

    return list;
  });

  // --- Computed Pagination ---
  displayedMails = computed(() => {
    const start = (this.page() - 1) * this.size();
    return this.filteredAndSortedMails().slice(start, start + this.size());
  });

  totalPages = computed(() => Math.ceil(this.filteredAndSortedMails().length / this.size()) || 1);

  // --- Initialization ---
  ngOnInit() { this.loadTrash(); }
  ngOnChanges() { this.loadTrash(); }

  loadTrash() {
    const userId = this.authService.user()?.id;
    if (!userId) return;
    // Fetching large limit for front-end processing
    this.trashService.getTrash(userId, 1, 1000, '', 'subject', false, 'any', 'date').subscribe({
      next: (data) => {
        this.allMails.set(data);
        this.selectedIds.set([]); 
      }
    });
  }

  // --- Event Handlers ---
  handleSearchChange(val: string) {
    this.searchQuery.set(val);
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

  handleRefresh() {
    this.selectedIds.set([]);
    this.loadTrash();
  }

  // --- Pagination Logic ---
  handleNextPage() { if (this.hasNextPage()) this.page.update(p => p + 1); }
  handlePrevPage() { if (this.hasPrevPage()) this.page.update(p => p - 1); }
  hasPrevPage = computed(() => this.page() > 1);
  hasNextPage = computed(() => (this.page() * this.size()) < this.filteredAndSortedMails().length);

  // --- Selection Logic ---
  toggleSelectAll() {
    if (this.isAllSelected()) this.selectedIds.set([]);
    else this.selectedIds.set(this.displayedMails().map(m => m.id));
  }

  isAllSelected(): boolean {
    return this.displayedMails().length > 0 && 
           this.displayedMails().every(m => this.selectedIds().includes(m.id));
  }

  toggleId(id: number) {
    this.selectedIds.update(ids => ids.includes(id) ? ids.filter(x => x !== id) : [...ids, id]);
  }

  // --- Actions ---
  handleBulkRestore() { this.performRestore(this.selectedIds()); }
  handleBulkDelete() { this.performDelete(this.selectedIds()); }
  handleSingleDelete(id: number) { this.performDelete([id]); }

  private performDelete(ids: number[]) {
    if(confirm(`Permanently delete ${ids.length} item(s)?`)) {
      this.trashService.bulkDeleteForever(ids).subscribe({ next: () => this.handleRefresh() });
    }
  }

  private performRestore(ids: number[]) {
    this.trashService.bulkRestore(ids).subscribe({ next: () => this.handleRefresh() });
  }

  openMail(mail: DeletedMailDTO) { this.selectedMail.set(mail); }
  closeMail() { this.selectedMail.set(null); this.loadTrash(); }

  getParticipantDisplay(mail: DeletedMailDTO): string {
    if (mail.source === MailSource.SENT) {
      const count = mail.receivers?.length || 0;
      return `To: ${mail.receivers?.[0]?.name || 'Unknown'}${count > 1 ? ' +' + (count - 1) : ''}`;
    }
    return `From: ${mail.sender?.name || 'Unknown'}`;
  }
}