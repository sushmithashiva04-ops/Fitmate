import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { jwtDecode } from 'jwt-decode';
import { AuthService } from '../../services/auth.service';

interface JwtPayload {
  sub: string;
  role: string;
  name?: string;
  exp?: number;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css'],
})
export class LandingComponent implements OnInit {
  userRole: string = '';
  username: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    try {
      const decoded = jwtDecode<JwtPayload>(token);
      this.username = decoded.name || decoded.sub || 'User';
      this.userRole = decoded.role?.toUpperCase() || 'USER';

      // ✅ Only navigate when user lands on `/landing` without a child route
      if (this.router.url === '/landing' || this.router.url === '/landing/') {
        this.router.navigate(['landing/dashboard']);
      }
    } catch (error) {
      console.error('Invalid JWT:', error);
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  navigateToProfile(): void {
    this.router.navigate(['/profile']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
