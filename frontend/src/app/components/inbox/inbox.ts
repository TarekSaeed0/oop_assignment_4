import { Mail } from './../../types/mail';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { MailService } from '../../services/mail-service';
import { MailResponce } from '../../types/mail';
import { MailInbox } from "../mail-inbox/mail-inbox";
import { AuthenticationService } from '../../services/authentication.service';
import { Priority } from './../../types/mail';
import { HomeComponent } from '../home/home.component';
@Component({
  selector: 'app-inbox',
  imports: [MailInbox],
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
      if (this.currentMailId() == null) {
        this.mailService.getInbox(authSevice.user()?.id || 0, this.page(), this.size(), "", "", false, "any").subscribe({
          next: (d: any) => {
            console.log(d);
            this.inbox.set(d);
          }
        })
      }
    })
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
    return this.selectedMail().length == this.inbox().length
  }
  handleRefresh = () => {
    if (this.currentMailId() == null) {
      this.mailService.getInbox(this.authService?.user()?.id || 0, this.page(), this.size(),
        this.home?.searchBy() || "", "", false, "any"
      ).subscribe({
        next: (d: any) => {
          console.log(d);
          this.inbox.set(d);
        }
      })
    }
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
  inbox = signal<MailResponce>([])
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
