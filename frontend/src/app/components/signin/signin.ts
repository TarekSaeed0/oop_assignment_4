import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-signin',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signin.html',
  styleUrl: './signin.css',
})
export class Signin {
  private formBuilder = inject(FormBuilder);

  form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  showPassword = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  private authenticationService = inject(AuthenticationService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  error = null;

  onSubmit() {
    this.error = null;
    if (this.form.valid) {
      const value = this.form.getRawValue();
      this.authenticationService
        .signin({
          email: value.email,
          password: value.password,
        })
        .subscribe({
          next: () => {
            const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
            this.router.navigateByUrl(returnUrl);
          },
          error: (err) => {
            this.error = err.error.message || 'An error occurred during sign in.';
          },
        });
    }
  }
}
