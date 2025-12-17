import {AfterViewInit, Component, inject, OnInit, signal} from '@angular/core';
import {HomeComponent} from '../home/home.component';
import {DatePipe, SlicePipe} from '@angular/common';
import {MailInbox} from '../mail-inbox/mail-inbox';
import {MoveToMenuComponent} from '../move-to-menu/move-to-menu';
import {DraftsResponse} from '../../types/mail';
import {DraftsService} from '../../services/draftsService/drafts-service';
import {Compose} from '../compose/compose'; // start
import { Injectable } from '@angular/core';
import {MailService} from '../../services/mail-service';

@Injectable({
  providedIn: 'root'
})
export class RefreshService {
  draftService = inject(DraftsService)
  drafts = signal<DraftsResponse>([]);

  handleRefresh() {
    this.draftService.getDrafts().subscribe({
      next: (data: any) => {
        console.log(data)
        this.drafts.set(data as any)
      }
    })
  }
}

@Component({
  selector: 'app-drafts',
  imports: [
    SlicePipe,
  ],
  templateUrl: './drafts.html',
  styleUrl: './drafts.css',
})
export class Drafts implements AfterViewInit, OnInit{
  homeComponent: undefined | HomeComponent;
  compose: Compose | undefined;
  mailService = inject(MailService)
  constructor(homeComponent: HomeComponent) {
    this.homeComponent = homeComponent
    // this.compose = compose
    // console.log(compose.draftId())
    homeComponent.closeCompose();
  }
  refreshService = inject(RefreshService)
  // drafts = signal<DraftsResponse>([])
  drafts = this.refreshService.drafts
  currentDraftId = signal<number | null>(null);
  selectedDrafts = signal<number[]>([])
  draftService = inject(DraftsService)

  ngOnInit(): void {
    this.mailService.refreshAfterComposeObservable
      .subscribe(
        () => {
          this.handleRefresh()
        }
      )
  }

  ngAfterViewInit(): void {
    if(this.currentDraftId() == null) {
      this.refreshService.handleRefresh()
    }
  }
  isAllSelected() {
    return this.selectedDrafts().length == this.refreshService.drafts().length && this.refreshService.drafts().length != 0
  }

  handleSelectAll() {
    this.selectedDrafts.set(this.refreshService.drafts().map((draft) => draft.id))
  }

  // handleRefresh() {
  //   this.draftService.getDrafts().subscribe({
  //     next: (data: any) => {
  //       console.log(data)
  //       this.drafts.set(data as any)
  //     }
  //   })
  // }
  handleRefresh() {
    this.refreshService.handleRefresh();
  }

  handleBulkDelete() {
    // todo:
  }


  handleDeleteDraft(id: number) {
    this.draftService.deleteDraft(id).subscribe({
      next: () => {
        this.refreshService.handleRefresh()
      }
    });
  }

  handleClickDraft(id: number) {
    this.draftService.putDraft(id)
  }

  handleCheckDraft(id: number) {
    this.selectedDrafts.update(l => [...l, id])
  }
}
