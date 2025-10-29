import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminDashboardService {
  private apiUrl = 'http://localhost:8080/api/admin/metrics';

  constructor(private http: HttpClient) {}

  getTotalWorkouts(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-workouts`);
  }

  getScheduledWorkouts(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/scheduled-workouts`);
  }

  getCompletionRate(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/completion-rate`);
  }
}
