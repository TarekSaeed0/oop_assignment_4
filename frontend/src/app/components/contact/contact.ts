import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ContactService } from '../../services/contact-service';
import { AuthenticationService } from '../../services/authentication.service';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './contact.html',
  styleUrls: ['./contact.css'],
})
export class Contact implements OnInit {
  private contactService = inject(ContactService);
  private authService = inject(AuthenticationService);

  contacts = signal<any[]>([]);
  showForm = signal(false);

  name = signal('');
  emailInput = signal('');
  emailList = signal<string[]>([]);

  isEdit = signal(false);
  editContactId = signal<number | null>(null);

  ngOnInit(): void {
    this.authService.loadUser().subscribe((user) => {
      if (user) this.loadContacts(user.id);
    });
  }

  toggleForm() {
    this.showForm.update((v) => !v);

    if (!this.showForm()) this.resetForm();
  }

  loadContacts(userId: number) {
    this.contactService.getContacts(userId).subscribe((data: any) => {
      this.contacts.set(data);
      console.log(data);
    });
    console.log(this.contacts());
  }

  addEmail() {
    if (this.emailInput().trim()) {
      this.emailList.update((list) => [...list, this.emailInput().trim()]);
      this.emailInput.set('');
    }
  }

  removeEmail(i: number) {
    this.emailList.update((list) => list.filter((_, index) => index !== i));
  }

  saveContact() {
    const user = this.authService.user();
    if (!user) return;

    if (this.isEdit()) {
      this.contactService
        .editContact(user.id, this.editContactId()!, this.name(), this.emailList())
        .subscribe(() => {
          this.loadContacts(user.id);
          this.resetForm();
        });
    } else {
      this.contactService.createContact(user.id, this.name(), this.emailList()).subscribe(() => {
        this.loadContacts(user.id);
        this.resetForm();
      });
    }
  }

  delete(contact: any) {
    const user = this.authService.user();
    if (!user) return;

    if (!contact.contactId) {
      console.error('contactId undefined', contact);
      return;
    }

    this.contactService.deleteContact(contact.contactId).subscribe({
      next: () => this.loadContacts(user.id),
      error: (err) => console.error(err),
    });
  }

  edit(contact: any) {
    this.showForm.set(true);
    this.isEdit.set(true);
    this.editContactId.set(contact.contactId);

    this.name.set(contact.name);
    this.emailList.set([...contact.contactEmails]);
  }

  resetForm() {
    this.name.set('');
    this.emailInput.set('');
    this.emailList.set([]);
    this.isEdit.set(false);
    this.editContactId.set(null);
  }
}
