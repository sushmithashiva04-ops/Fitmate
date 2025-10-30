import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminWorkoutService {
  private baseUrl = '${environment.apiUrl}/admin/workout'; // ✅ backend base path

  constructor(private http: HttpClient) {}

  // ➕ Add Workout
  addWorkout(workout: any): Observable<string> {
    return this.http.post(this.baseUrl + '/add', workout, {
      responseType: 'text',
    });
  }

  // ✏️ Update Workout
  updateWorkout(id: number, workout: any): Observable<string> {
    return this.http.put(this.baseUrl + `/update/${id}`, workout, {
      responseType: 'text',
    });
  }

  // ❌ Delete Workout
  deleteWorkout(id: number): Observable<string> {
    return this.http.delete(this.baseUrl + `/delete/${id}`, {
      responseType: 'text',
    });
  }
}
