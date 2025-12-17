import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserFolderService } from '../../services/UserFolderService';

@Component({
  selector: 'app-create-folder-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-folder-dialog.html',
})
export class CreateFolderDialogComponent {
  @Input() userId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() folderCreated = new EventEmitter<void>();

  folderName = '';
  errorMessage = '';
  isLoading = false;

  private userFolderService = inject(UserFolderService);

  tryCreateFolder() {
    if (!this.folderName.trim()) return;

    this.isLoading = true;
    this.errorMessage = '';

    // 1. Check if name is valid
    this.userFolderService.isValidNameForUserFolder(this.folderName, this.userId).subscribe({
      next: (isValid) => {
        if (isValid) {
          // 2. Create the folder
          this.createFolder();
        } else {
          this.errorMessage = 'Folder name already exists.';
          this.isLoading = false;
        }
      },
      error: () => {
        this.errorMessage = 'Network error. Please try again.';
        this.isLoading = false;
      }
    });
  }

  private createFolder() {
  this.userFolderService.createFolder(this.folderName, this.userId).subscribe({
    next: () => {
      this.isLoading = false; // Good practice to reset
      this.folderCreated.emit();
      this.close.emit();
    },
    error: () => {
      this.errorMessage = 'Could not create folder.';
      this.isLoading = false;
    }
  });
}
}
