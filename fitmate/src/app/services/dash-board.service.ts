import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private baseUrl = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  getWorkoutCounts(): Observable<any> {
    return this.http.get(`${this.baseUrl}/counts`);
  }

  getTotalDuration(): Observable<any> {
    return this.http.get(`${this.baseUrl}/total-duration`);
  }

  getTotalCalories(): Observable<any> {
    return this.http.get(`${this.baseUrl}/total-calories`);
  }

  getStreak(): Observable<any> {
    return this.http.get(`${this.baseUrl}/streak`);
  }

  getWorkoutTypes(): Observable<any> {
    return this.http.get(`${this.baseUrl}/workout-types`);
  }

  getWeeklyStats(): Observable<any> {
    return this.http.get(`${this.baseUrl}/weekly-stats`);
  }
}
