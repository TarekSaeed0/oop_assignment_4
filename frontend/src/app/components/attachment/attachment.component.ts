import { Component, inject, input, output } from '@angular/core';
import { AttachmentService } from '../../services/attachment.service';
import { Attachment } from '../../types/mail';

@Component({
  selector: 'app-attachment',
  imports: [],
  templateUrl: './attachment.component.html',
  styleUrl: './attachment.component.css',
})
export class AttachmentComponent {
  private attachmentService = inject(AttachmentService);

  attachment = input.required<Attachment>();
  preview = input<boolean>(false);
  removable = input<boolean>(false);
  onRemove = output<Attachment>();

  attachmentName() {
    return this.attachment().name;
  }

  attachmentSize() {
    const units = ['B', 'K', 'M', 'G', 'T'];

    let size = this.attachment().size;
    let unit = 0;

    while (size >= 1024 && unit < units.length - 1) {
      size = size / 1024;
      unit++;
    }

    return `${Math.round(size)}${units[unit]}`;
  }

  attachmentDownloadUrl() {
    return this.attachmentService.getAttachmentUrl(this.attachment().id) + '/download';
  }

  attachmentViewUrl() {
    return this.attachmentService.getAttachmentUrl(this.attachment().id) + '/view';
  }

  isAttachmentImage() {
    return !!this.attachment().type.match(/^image/);
  }
}
