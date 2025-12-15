import {AfterViewInit, Component, signal} from '@angular/core';
import {HomeComponent} from '../home/home.component';
import {DatePipe, SlicePipe} from '@angular/common';
import {MailInbox} from '../mail-inbox/mail-inbox';
import {MoveToMenuComponent} from '../move-to-menu/move-to-menu';
import {DraftsResponse} from '../../types/mail';

@Component({
  selector: 'app-drafts',
  imports: [
    DatePipe,
    MailInbox,
    MoveToMenuComponent,
    SlicePipe
  ],
  templateUrl: './drafts.html',
  styleUrl: './drafts.css',
})
export class Drafts implements AfterViewInit{
  homeComponent: undefined | HomeComponent;
  constructor(homeComponent: HomeComponent) {
    this.homeComponent = homeComponent
    homeComponent.closeCompose();
  }
  drafts = signal<DraftsResponse>([])
  currentDraftId = signal<number | null>(null);
  selectedDrafts = signal<number[]>([])

  ngAfterViewInit(): void {
    if(this.currentDraftId() != null) {
      this.handleRefresh()
    }
  }
  isAllSelected() {
    return this.selectedDrafts().length == this.drafts().length && this.drafts().length != 0
  }

  handleSelectAll() {
    this.selectedDrafts.set(this.drafts().map((draft) => draft.id))
  }

  handleRefresh() {

  }

  handleBulkDelete() {
    // todo:
  }


  handleDeleteDraft(id: number) {

  }

  handleClickDraft(id: number) {

  }

  handleCheckDraft(id: number) {
    this.selectedDrafts.update(l => [...l, id])
  }
}
