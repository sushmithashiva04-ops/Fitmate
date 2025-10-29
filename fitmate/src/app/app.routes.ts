import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { authGuard } from './Gaurds/auth.guard';

import { ProfileComponent } from './components/profile/profile.component';
import { LandingComponent } from './components/landing/landing.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { WorkoutComponent } from './components/workout/workout.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { AdminWorkoutComponent } from './components/admin-workout/admin-workout.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'home',
    component: LandingComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent }, // can replace with dashboard comp later
      { path: 'workout', component: WorkoutComponent },
      { path: 'calendar', component: CalendarComponent },
    ],
  },
  { path: 'profile', component: ProfileComponent },
  { path: 'Admin', component: AdminWorkoutComponent },

  { path: 'AdminDashboard', component: AdminDashboardComponent },
  { path: '', redirectTo: '/profile', pathMatch: 'full' },

  // { path: '**', redirectTo: 'login' },
];
