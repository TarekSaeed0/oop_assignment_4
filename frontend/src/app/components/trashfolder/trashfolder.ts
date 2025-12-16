import { Component, inject, Input, OnChanges, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TrashService } from '../../services/trash-service'; 
import { DeletedMailDTO, MailSource } from '../../types/trash';
import { AuthenticationService } from '../../services/authentication.service';
import { MailInTrashComponent } from '../mail-in-trash/mail-in-trash';

@Component({
  selector: 'app-trashfolder',
  standalone: true,
  imports: [DatePipe, MailInTrashComponent],
  templateUrl: './trashfolder.html', 
  styleUrl: './trashfolder.css'
})
export class TrashfolderComponent implements OnInit, OnChanges {
  private trashService = inject(TrashService);
  private authService = inject(AuthenticationService);

  mails = signal<DeletedMailDTO[]>([]);
  selectedMail = signal<DeletedMailDTO | null>(null);
  selectedIds = signal<number[]>([]); 

  @Input() searchBy = ""; 

  page = 1;
  size = 10;
  filterBy = 'subject'; 
  priority = 'any';
  hasAttachment = false;

  ngOnInit() {
    this.loadTrash();
  }
handleRefresh() {
    this.selectedIds.set([]); // Clear selection on refresh to avoid bugs
    this.loadTrash();
  }
  
  ngOnChanges() {
    this.loadTrash();
  }

  loadTrash() {
    const userId = this.authService.user()?.id;
    if (!userId) return;

    this.trashService.getTrash(
      userId, this.page, this.size, this.searchBy, 
      this.filterBy, this.hasAttachment, this.priority, 'date' 
    ).subscribe({
      next: (data) => {
        this.mails.set(data);
        this.selectedIds.set([]); 
      },
      error: (err) => console.error('Error loading trash', err)
    });
  }

  // --- SELECTION LOGIC ---
  toggleSelectAll() {
    if (this.isAllSelected()) {
      this.selectedIds.set([]);
    } else {
      this.selectedIds.set(this.mails().map(m => m.id));
    }
  }

  isAllSelected(): boolean {
    return this.mails().length > 0 && this.selectedIds().length === this.mails().length;
  }

  toggleId(id: number) {
    this.selectedIds.update(ids => {
      if (ids.includes(id)) {
        return ids.filter(x => x !== id);
      } else {
        return [...ids, id];
      }
    });
  }

  // --- ACTION LOGIC (All using Bulk) ---

  // 1. Restore Checked Items
  handleBulkRestore() {
    const ids = this.selectedIds();
    if (ids.length === 0) return;
    this.performRestore(ids);
  }

  // 2. Delete Checked Items
  handleBulkDelete() {
    const ids = this.selectedIds();
    if (ids.length === 0) return;
    this.performDelete(ids);
  }

  // 3. Delete Single Item (e.g. from row hover button)
  handleSingleDelete(id: number) {
    this.performDelete([id]);
  }

  // Helper for Delete
  private performDelete(ids: number[]) {
    if(confirm(`Permanently delete ${ids.length} item(s)?`)) {
      this.trashService.bulkDeleteForever(ids).subscribe({
        next: () => {
          this.loadTrash();
        },
        error: (err) => console.error(err)
      });
    }
  }

  // Helper for Restore
  private performRestore(ids: number[]) {
    this.trashService.bulkRestore(ids).subscribe({
      next: () => {
        this.loadTrash();
      },
      error: (err) => console.error(err)
    });
  }

  // --- DETAIL VIEW ---
  openMail(mail: DeletedMailDTO) {
    this.selectedMail.set(mail);
  }

  closeMail() {
    this.selectedMail.set(null);
    this.loadTrash();
  }

  getParticipantDisplay(mail: DeletedMailDTO): string {
    if (mail.source === MailSource.SENT) {
      const count = mail.receivers.length;
      if (count === 0) return 'No Receivers';
      if (count === 1) return `To: ${mail.receivers[0].name}`;
      return `To: ${mail.receivers[0].name} +${count - 1}`;
    } else {
      return `From: ${mail.sender.name}`;
    }
  }
}