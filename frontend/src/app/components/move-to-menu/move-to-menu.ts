import { Component, signal, computed, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserFolder } from '../../services/UserFolderService'; // Import your interface

@Component({
  selector: 'app-move-to-menu',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './move-to-menu.html',
})
export class MoveToMenuComponent {
  // Inputs & Outputs
  private _userFolders = signal<UserFolder[]>([]);

  @Input({ required: true }) 
  set userFolders(value: UserFolder[]) {
    this._userFolders.set(value);
  }

  @Output() folderChange = new EventEmitter<string>();

  // State
  isMenuOpen = signal(false);
  searchQuery = signal('');
  selectedFolder = signal<string | null>(null);

  // Filter Logic
  filteredFolders = computed(() => {
    const folders = this._userFolders();
    const query = this.searchQuery().toLowerCase();
    if (!query) return folders;
    return folders.filter(f => f.name?.toLowerCase().includes(query));
  });

  toggleMenu() {
    this.isMenuOpen.update(v => !v);
    if (this.isMenuOpen()) this.searchQuery.set('');
  }

  closeMenu() {
    this.isMenuOpen.set(false);
  }

  onSelectFolder(folderName: string) {
    this.folderChange.emit(folderName);
    this.closeMenu();
  }
}