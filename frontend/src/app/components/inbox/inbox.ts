import { InboxMail } from './../../types/mail';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { InboxMailResponce } from '../../types/mail';
import { MailInbox } from "../mail-inbox/mail-inbox";
import { AuthenticationService } from '../../services/authentication.service';
import { HomeComponent } from '../home/home.component';
import { DatePipe, SlicePipe } from '@angular/common';
@Component({
  selector: 'app-inbox',
  imports: [MailInbox, DatePipe, DatePipe, SlicePipe],
  templateUrl: './inbox.html',
  styleUrl: './inbox.css',
})
export class Inbox implements OnInit {

  mailService = inject(MailService)
  authService: undefined | AuthenticationService = undefined;
  home: HomeComponent | undefined;
  constructor(authSevice: AuthenticationService, home: HomeComponent) {
    this.authService = authSevice
    this.home = home;
    effect(() => {
      this.handleRefresh()
    })
  }
  handleRefresh = () => {
    if (this.currentMailId() == null) {
      this.mailService.getInbox(this.authService?.user()?.id || 0, this.page(), this.size(),
        this.home?.searchBy() || "", this.filterBy(), this.hasAttachments(), this.priority(), this.sortBy()
      ).subscribe({
        next: (d: any) => {
          console.log(d);
          this.inbox.set(d);
        }
      })
    }
  }
  hasAttachments = signal<boolean>(false);
  priority = signal("any");
  filterBy = signal("any");
  sortBy = signal("date");
  handleAttchmentToggle(event: any) {
    this.hasAttachments.update((h) => !h)
  }
  handlePriorityChange($event: any) {
    this.priority.set($event.target.value)

  }
  handleSortChange($event: any) {
    this.sortBy.set($event.target.value)
  }
  handleFilterChange($event: any) {
    this.filterBy.set($event.target.value)
  }
  handleDelete = (arg0: number) => {
    this.mailService.deleteMail(arg0 as number)
      .subscribe({
        next: (value) => {
          console.log(value);
        }
      })
  }
  handleNextPage = () => {
    if (this.hasNextPage()) {
      this.page.update((p) => p + 1);
      this.handleRefresh()
    }
  }
  hasPrevPage() {
    return this.page() > 1
  }
  hasNextPage() {
    return this.inbox().length == this.size()
  }
  handlePrevPage() {
    if (this.hasPrevPage()) {
      this.page.update((p) => p - 1);
      this.handleRefresh()
    }
  }
  getPageInfo() {
  }
  handleBulkMove() {
  }
  handleSelectAll = () => {
    if (this.isAllSelected()) {
      this.selectedMail.set([])
    } else {
      this.selectedMail.set(this.inbox().map((e) => e.id))
    }
  }
  isAllSelected() {
    return (this.selectedMail().length == this.inbox().length) && (this.inbox().length != 0)
  }

  handleBulkDelete() {
    const mailList = this.selectedMail()
    this.mailService.bulkDelete(mailList)
      .subscribe({
        next: () => {
          console.log("deleted: " + mailList.toString());
          this.handleRefresh()
        }
      })
    this.selectedMail.set([]);
  }
  ngOnInit(): void {

  }
  inbox = signal<InboxMailResponce>([])
  selectMail(id: number) {
    this.currentMailId.set(id)
  }
  currentMailId = signal<null | number>(null);
  page = signal<number>(1);
  size = signal<number>(10);
  handleClickEmail(id: number) {
    this.currentMailId.set(id)
  }
  selectedMail = signal<number[]>([])
  handleSelectMail = (id: number) => {
    this.selectedMail.update((l) => {
      if (l.includes(id)) return l.filter((d) => d != id)
      else {
        l.push(id)
        return l
      }
    })
  }

}
