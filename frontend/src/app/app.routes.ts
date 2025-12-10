import { Routes } from '@angular/router';
import { Signin } from './components/signin/signin';
import { Signup } from './components/signup/signup';
import { HomeComponent } from './components/home/home.component';
import { authenticationGuard } from './guards/authentication.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, title: 'Home', canActivate: [authenticationGuard] },
  { path: 'signup', component: Signup, title: 'Sign Up' },
  { path: 'signin', component: Signin, title: 'Sign In' },
  { path: '**', redirectTo: '' },
];
