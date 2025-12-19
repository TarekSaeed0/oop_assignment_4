import { Component, effect, inject, signal, Input, OnInit } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { InboxMailResponce, SentMailResponce } from '../../types/mail';
import { MailInbox } from '../mail-inbox/mail-inbox';
import { AuthenticationService } from '../../services/authentication.service';
import { MailInSent } from '../mail-in-sent/mail-in-sent';
import { HomeComponent } from '../home/home.component';
import { DatePipe } from '@angular/common';
import { MoveToMenuComponent } from '../move-to-menu/move-to-menu';
import { UserFolder, UserFolderService } from '../../services/UserFolderService';

@Component({
  selector: 'app-sent',
  imports: [MailInSent, DatePipe, MoveToMenuComponent],
  templateUrl: './sent.html',
  styleUrl: './sent.css',
})
export class Sent implements OnInit {
  private userFolderService = inject(UserFolderService); // Inject this
  private authServicee = inject(AuthenticationService); //i know its a duplicate
  mailService = inject(MailService);
  @Input() userFolders: UserFolder[] = [];

  authService: undefined | AuthenticationService = undefined;
  home: HomeComponent | undefined;
  constructor(authSevice: AuthenticationService, home: HomeComponent) {
    this.authService = authSevice;
    this.home = home;
    effect(() => {
      this.handleRefresh();
    });
  }

  ngOnInit(): void {
    this.mailService.refreshAfterComposeObservable.subscribe(() => {
      console.log('refreshed in sent');
      this.handleRefresh();
    });
  }

  handleRefresh = () => {
    if (this.currentMailId() == null) {
      this.mailService
        .getSent(
          this.authService?.user()?.id || 0,
          this.page(),
          this.size(),
          this.home?.searchBy() || '',
          this.filterBy(),
          this.hasAttachments(),
          this.priority(),
          this.sortBy(),
        )
        .subscribe({
          next: (d: any) => {
            console.log(d);
            this.sent.set(d);
          },
        });
    }
  };
  hasAttachments = signal<boolean>(false);
  priority = signal('any');
  filterBy = signal('any');
  sortBy = signal('date');
  handleAttchmentToggle(event: any) {
    this.hasAttachments.update((h) => !h);
  }
  handlePriorityChange($event: any) {
    this.priority.set($event.target.value);
  }
  handleSortChange($event: any) {
    this.sortBy.set($event.target.value);
  }
  handleFilterChange($event: any) {
    this.filterBy.set($event.target.value);
  }
  handleDelete(id: number) {
    this.mailService.deleteMail(id).subscribe({
      next: () => {
        this.handleRefresh();
      },
    });
  }
  totalPages() {
    return Math.ceil(this.sent().length / this.size()) || 1;
  }
  handleNextPage = () => {
    if (this.hasNextPage()) {
      this.page.update((p) => p + 1);
      this.handleRefresh();
    }
  };
  hasPrevPage() {
    return this.page() > 1;
  }
  hasNextPage() {
    return this.sent().length == this.size();
  }
  handlePrevPage() {
    if (this.hasPrevPage()) {
      this.page.update((p) => p - 1);
      this.handleRefresh();
    }
  }

  handleSelectAll = () => {
    if (this.isAllSelected()) {
      this.selectedMail.set([]);
    } else {
      this.selectedMail.set(this.sent().map((e) => e.id));
    }
  };
  isAllSelected() {
    return this.selectedMail().length == this.sent().length && this.sent().length != 0;
  }

  handleBulkDelete() {
    const mailList = this.selectedMail();
    this.mailService.bulkDelete(mailList).subscribe({
      next: () => {
        console.log('deleted: ' + mailList.toString());
        this.handleRefresh();
      },
    });
    this.selectedMail.set([]);
  }

  sent = signal<SentMailResponce>([]);

  currentMailId = signal<null | number>(null);
  page = signal<number>(1);
  size = signal<number>(10);
  handleClickEmail(id: number) {
    this.currentMailId.set(id);
  }
  selectedMail = signal<number[]>([]);
  handleSelectMail = (id: number) => {
    this.selectedMail.update((l) => {
      if (l.includes(id)) return l.filter((d) => d != id);
      else {
        l.push(id);
        return l;
      }
    });
    console.log(this.selectedMail());
  };
  handleMoveToFolder(folderName: string) {
    const userId = this.authServicee.user()?.id;
    const mailIds = this.selectedMail();

    if (!userId || mailIds.length === 0) return;

    this.userFolderService.addToFolder(userId, folderName, mailIds).subscribe({
      next: (response) => {
        console.log('Moved successfully', response);
        this.selectedMail.set([]);
        this.handleRefresh();
      },
      error: (err) => console.error('Move failed', err),
    });
  }
}
