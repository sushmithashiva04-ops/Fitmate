import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private router: Router) {}

  // ✅ Login now uses 'name' and expects plain text token
  login(credentials: { name: string; password: string }): Observable<string> {
    return this.http
      .post(`${this.apiUrl}/auth/login`, credentials, { responseType: 'text' })
      .pipe(
        tap((token: string) => {
          if (token) {
            localStorage.setItem('token', token);
          }
        })
      );
  }

  // ✅ Register (no change needed)
  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, user, {
      responseType: 'text',
    });
  }

  getProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/profile/me`);
  }

  addProfile(details: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/profile`, details, {
      responseType: 'text',
    });
  }
  getUserEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/calendar/schedule`);
  }

  addAdminWorkout(workout: any) {
    return this.http.post(`${this.apiUrl}/admin/workout/add`, workout, {
      responseType: 'text',
    });
  }

  updateAdminWorkout(id: number, workout: any) {
    return this.http.put(`${this.apiUrl}/admin/workout/update/${id}`, workout, {
      responseType: 'text',
    });
  }

  deleteAdminWorkout(id: number) {
    return this.http.delete(`${this.apiUrl}/admin/workout/delete/${id}`, {
      responseType: 'text',
    });
  }
  updateProfile(details: any): Observable<string> {
    return this.http.put(`${this.apiUrl}/profile`, details, {
      responseType: 'text',
    });
  }
  getTodayWorkouts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workouts/today`, {});
  }

  getUserWorkouts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workouts/user`, {});
  }

  getAdminWorkouts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/workouts/admin`, {});
  }

  addWorkout(workout: any): Observable<string> {
    return this.http.post(`${this.apiUrl}/workouts/add`, workout, {
      responseType: 'text',
    });
  }
  
  updateWorkout(id: number, workout: any): Observable<string> {
    return this.http.put(`${this.apiUrl}/workouts/update/${id}`, workout, {
      responseType: 'text',
    });
  }
  rescheduleWorkout(workoutId: number): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/workouts/schedule-admin/${workoutId}`,
      {},
      {
        responseType: 'text',
      }
    );
  }
  markWorkoutComplete(id: number, calories: number) {
    return this.http.post<string>(
      `${this.apiUrl}/workouts/complete/${id}`,
      { caloriesBurned: calories },
      { responseType: 'text' as 'json' }
    );
  }

  deleteWorkout(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/workouts/delete/${id}`, {
      responseType: 'text',
    });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/admin/workout/delete/${id}`, {
      responseType: 'text',
    });
  }

  deleteExercise(workoutId: number, exerciseId: number): Observable<string> {
    return this.http.delete(
      `${this.apiUrl}/workouts/${workoutId}/exercise/${exerciseId}`,
      {
        responseType: 'text',
      }
    );
  }

  // ✅ Token utilities
  isTokenExpired(token: string): boolean {
    try {
      const decoded: any = jwtDecode(token);
      const expiry = decoded.exp * 1000;
      return Date.now() > expiry;
    } catch (err) {
      console.error('Invalid token', err);
      return true;
    }
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
  getCurrentUser(): string {
    const token = localStorage.getItem('token');
    if (!token) return '';
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub; // username or email depending on your JWT
  }
}
