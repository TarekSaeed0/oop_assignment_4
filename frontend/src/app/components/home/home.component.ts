import { Component, HostListener, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { Inbox } from '../inbox/inbox';
import { Sent } from '../sent/sent';
import { Contact } from '../contact/contact';
import { CreateFolderDialogComponent } from '../create-folder-dialog/create-folder-dialog';
import { UserFolder, UserFolderService } from '../../services/UserFolderService';
import { CustomFolderComponent } from '../custom-folder/custom-folder';
import { Compose } from '../compose/compose';
import { TrashfolderComponent } from '../trashfolder/trashfolder';
import { Drafts } from '../drafts/drafts';

@Component({
  imports: [
    RouterLink,
    Inbox,
    Sent,
    Contact,
    CreateFolderDialogComponent,
    CustomFolderComponent,
    Compose,
    Drafts,
    TrashfolderComponent,
  ],
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  selectedFolder = signal<'Inbox' | 'Sent' | 'Drafts' | 'Trash' | number | string>('Inbox');
  changeFolder = (folderName: string) => {
    this.selectedFolder.set(folderName);
  };
  folders = [
    {
      name: 'Inbox',
      icon: 'M2.25 13.5h3.86a2.25 2.25 0 0 1 2.012 1.244l.256.512a2.25 2.25 0 0 0 2.013 1.244h3.218a2.25 2.25 0 0 0 2.013-1.244l.256-.512a2.25 2.25 0 0 1 2.013-1.244h3.859m-19.5.338V18a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18v-4.162c0-.224-.034-.447-.1-.661L19.24 5.338a2.25 2.25 0 0 0-2.15-1.588H6.911a2.25 2.25 0 0 0-2.15 1.588L2.35 13.177a2.25 2.25 0 0 0-.1.661Z',
    },
    {
      name: 'Sent',
      icon: 'M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5',
    },
    {
      name: 'Drafts',
      icon: 'M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z',
    },
    {
      name: 'Trash',
      icon: 'm14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0',
    },
  ];

  isUserMenuOpen = false;
  ///start userfolder service methods
  private userFolderService = inject(UserFolderService);

  // 4. State for User Folders
  userFolders: UserFolder[] = [];
  isCreateFolderDialogOpen = false;

  ngOnInit() {
    this.refreshUserFolders();
  }

  // 5. Helper to get current User ID (Assuming Auth Service has it)
  // You might need to adjust this depending on how your Auth Service stores the ID
  get currentUserId(): number {
    return this.authenticationService.user()?.id || 0;
  }

  // 6. Dialog Management
  openCreateFolderDialog() {
    this.isCreateFolderDialogOpen = true;
  }

  closeCreateFolderDialog() {
    this.isCreateFolderDialogOpen = false;
  }

  // 7. Fetch Folders (Call this on Init and after creating a folder)
  refreshUserFolders() {
    if (this.currentUserId) {
      this.userFolderService.getUserFolders(this.currentUserId).subscribe({
        next: (folders) => {
          this.userFolders = folders;
        },
        error: (err) => console.error('Failed to load folders', err),
      });
    }
  }

  handleDeleteFolder(event: Event, folder: UserFolder) {
    // Prevent the click from selecting the folder immediately
    event.stopPropagation();

    if (!folder.name) return;

    if (
      confirm(
        `Are you sure you want to delete the folder "${folder.name}"? Mails inside will be kept but detached from the folder.`,
      )
    ) {
      this.userFolderService.deleteFolder(this.currentUserId, folder.name).subscribe({
        next: () => {
          // If we are currently looking at this folder, switch to Inbox
          if (this.selectedFolder() === folder.name) {
            this.selectedFolder.set('Inbox');
          }
          this.refreshUserFolders();
        },
        error: (err) => alert('Error deleting folder'),
      });
    }
  }

  handleRenameFolder(event: Event, folder: UserFolder) {
    event.stopPropagation();

    if (!folder.name) return;

    const newName = prompt('Enter new folder name:', folder.name);

    // Check if name is valid and actually changed
    if (newName && newName.trim() !== '' && newName !== folder.name) {
      this.userFolderService.renameFolder(this.currentUserId, folder.name, newName).subscribe({
        next: () => {
          // If we are currently inside this folder, update the view name
          if (this.selectedFolder() === folder.name) {
            this.selectedFolder.set(newName);
          }
          this.refreshUserFolders();
        },
        error: (err) => alert('Error renaming folder. Name might already exist.'),
      });
    }
  }

  ///done UserFolderService methods
  toggleUserMenu() {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  @HostListener('document:click', ['$event'])
  hideUserMenu(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('#user-menu, #user-menu-button')) {
      this.isUserMenuOpen = false;
    }
  }

  private authenticationService = inject(AuthenticationService);

  searchBy = signal<string>('');
  handleSearchTyping = (key: any) => {};
  handleSearchChange(event: any) {
    this.searchBy.set(event.target.value);
  }
  handleSearch = () => {};
  userName() {
    return this.authenticationService.user()?.name.split(' ').slice(0, 2).join(' ');
  }

  userInitials() {
    return this.authenticationService
      .user()
      ?.name.split(' ')
      .slice(0, 2)
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }

  userEmail() {
    return this.authenticationService.user()?.email;
  }

  userMenuButtonTitle() {
    return `${this.userName()}\n${this.userEmail()}`;
  }

  private router = inject(Router);

  signOut() {
    this.authenticationService.signout().subscribe(() => this.router.navigateByUrl('/signin'));
  }

  //! -------------------- Contact Menu ------------------
  isContactMenuOpen = false;

  toggleContactMenu() {
    this.isContactMenuOpen = !this.isContactMenuOpen;
  }

  // ! -------------------- Compose ------------------
  isComposeOpen = false;
  openCompose() {
    this.isComposeOpen = true;
  }
  closeCompose() {
    this.isComposeOpen = false;
    return true;
  }
}
