import { Component, inject, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TrashService } from '../../services/trash-service';
import { DeletedMailDTO, MailSource } from '../../types/trash';
import { AttachmentComponent } from '../attachment/attachment.component';

@Component({
  selector: 'app-mail-in-trash',
  standalone: true,
  imports: [DatePipe, AttachmentComponent],
  templateUrl: './mail-in-trash.html',
  styleUrl: './mail-in-trash.css' 
})
export class MailInTrashComponent {
  private trashService = inject(TrashService);

  mail = input.required<DeletedMailDTO>();
  back = output<void>();

  handleBack() {
    this.back.emit();
  }

  // Restore Logic (Wraps ID in array)
  onRestore() {
    this.trashService.bulkRestore([this.mail().id]).subscribe({
      next: () => {
        console.log('Restored');
        this.handleBack();
      },
      error: (err) => console.error(err)
    });
  }

  // Delete Forever Logic (Wraps ID in array)
  onDeleteForever() {
    if(confirm('Are you sure? This cannot be undone.')) {
      this.trashService.bulkDeleteForever([this.mail().id]).subscribe({
        next: () => {
          console.log('Deleted Forever');
          this.handleBack();
        },
        error: (err) => console.error(err)
      });
    }
  }

  get isSentByMe(): boolean {
    return this.mail().source === MailSource.SENT;
  }
}