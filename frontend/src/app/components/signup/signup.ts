import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signup',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  private authenticationService = inject(AuthenticationService);
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    name: ['', Validators.required],
    password: ['', Validators.required],
  });

  showPassword = false;

  error = null;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    this.error = null;
    if (this.form.valid) {
      const value = this.form.getRawValue();
      this.authenticationService
        .signup({
          name: value.name,
          email: value.email,
          password: value.password,
        })
        .subscribe({
          next: () => this.router.navigateByUrl('/signin'),
          error: (err) => {
            this.error = err.error.message || 'An error occurred during sign in.';
          },
        });
    }
  }
}
