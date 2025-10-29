import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ExerciseService {
  private baseUrl = 'http://localhost:8080/api/exercises';

  constructor(private http: HttpClient) {}

  uploadExercise(formData: FormData): Observable<string> {
    return this.http.post(`${this.baseUrl}/uploadExercise`, formData, {
      responseType: 'text',
    });
  }
  updateExercise(exerciseId: number, formData: FormData): Observable<any> {
    return this.http.put(`${this.baseUrl}/exercises/${exerciseId}`, formData, {
      responseType: 'text',
    });
  }

  /**
   * ✅ Delete an exercise by ID
   */
  deleteExercise(exerciseId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${exerciseId}`, {
      responseType: 'text',
    });
  }
  getAllExercises(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/exercises`);
  }
}
