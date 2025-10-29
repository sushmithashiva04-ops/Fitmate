import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMsg: string = '';

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    // ✅ Match backend field names
    this.loginForm = this.fb.group({
      name: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  login() {
    if (this.loginForm.invalid) return;

    this.auth.login(this.loginForm.value).subscribe({
      next: (token: string) => {
        if (token) {
          localStorage.setItem('token', token);

          try {
            const decoded: any = jwtDecode(token);
            localStorage.setItem('username', decoded.username || decoded.sub);
            localStorage.setItem('role', decoded.role);
          } catch (e) {
            console.error('Error decoding token', e);
          }

          this.router.navigate(['/home']);
        } else {
          this.errorMsg = 'Login failed: No token received';
        }
      },
      error: (err) => {
        this.errorMsg = err.error || 'Invalid username or password';
      },
    });
  }
  goToRegister() {
    this.router.navigate(['/register']);
  }
}
