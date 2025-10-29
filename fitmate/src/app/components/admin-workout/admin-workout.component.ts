import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { AdminWorkoutService } from '../../services/admin-worout.service';
import { ExerciseService } from '../../services/exercise.service';

@Component({
  selector: 'app-admin-workout',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './admin-workout.component.html',
  styleUrls: ['./admin-workout.component.css'],
})
export class AdminWorkoutComponent implements OnInit {
  showForm = false;
  showExerciseForm = false;
  isEditMode = false;
  isExerciseEditMode = false;

  workoutForm!: FormGroup;
  exerciseForm!: FormGroup;

  workouts: any[] = [];
  exercisesFromDB: any[] = [];
  editIndex: number | null = null;
  editExerciseId: number | null = null;

  exerciseList: string[] = [
    'Push Ups',
    'Squats',
    'Lunges',
    'Burpees',
    'Plank',
    'Jumping Jacks',
    'Crunches',
    'Bench Press',
    'Deadlift',
    'Pull Ups',
  ];

  constructor(
    private fb: FormBuilder,
    private adminWorkoutService: AdminWorkoutService,
    private authService: AuthService,
    private exerciseService: ExerciseService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.initExerciseForm();
    this.loadAdminWorkouts();
    this.loadExercises();
  }

  initForm() {
    this.workoutForm = this.fb.group({
      name: ['', Validators.required],
      type: ['', Validators.required],
      duration: ['', Validators.required],
      exercises: this.fb.array([]),
    });
  }

  initExerciseForm() {
    this.exerciseForm = this.fb.group({
      name: ['', Validators.required],
      file: [null],
    });
  }

  get exercises(): FormArray {
    return this.workoutForm.get('exercises') as FormArray;
  }

  // ✅ Load workouts and exercises
  loadAdminWorkouts() {
    this.authService.getAdminWorkouts().subscribe({
      next: (data) => (this.workouts = data),
      error: () => alert('Failed to load workouts'),
    });
  }

  loadExercises() {
    this.exerciseService.getAllExercises().subscribe({
      next: (data: any[]) => {
        this.exercisesFromDB = data;
        data.forEach((ex) => {
          if (!this.exerciseList.includes(ex.name)) {
            this.exerciseList.push(ex.name);
          }
        });
      },
      error: (err) => console.error('Failed to load exercises', err),
    });
  }

  // ✅ Workout management
  openAddWorkoutForm() {
    this.showForm = true;
    this.isEditMode = false;
    this.workoutForm.reset();
    this.exercises.clear();
    this.addExercise();
  }

  addExercise() {
    const exerciseGroup = this.fb.group({
      name: ['', Validators.required],
      sets: ['', Validators.required],
      reps: ['', Validators.required],
      weight: ['', Validators.required],
    });
    this.exercises.push(exerciseGroup);
  }

  removeExercise(index: number) {
    this.exercises.removeAt(index);
  }

  saveWorkout() {
    if (this.workoutForm.invalid) {
      alert('Please fill all fields correctly!');
      return;
    }

    const workoutData = this.workoutForm.value;

    if (this.isEditMode && this.editIndex !== null) {
      const workoutId = this.workouts[this.editIndex].id;
      this.adminWorkoutService.updateWorkout(workoutId, workoutData).subscribe({
        next: (res) => {
          alert(res);
          this.loadAdminWorkouts();
          this.resetForm();
        },
        error: (err) => alert('Error updating workout: ' + err.error),
      });
    } else {
      this.adminWorkoutService.addWorkout(workoutData).subscribe({
        next: (res) => {
          alert(res);
          this.loadAdminWorkouts();
          this.resetForm();
        },
        error: (err) => alert('Error adding workout: ' + err.error),
      });
    }
  }

  editWorkout(index: number) {
    const workout = this.workouts[index];
    this.editIndex = index;
    this.isEditMode = true;
    this.showForm = true;

    this.workoutForm.patchValue({
      name: workout.name,
      type: workout.type,
      duration: workout.duration,
    });

    this.exercises.clear();
    workout.exercises.forEach((ex: any) => {
      const group = this.fb.group({
        name: [ex.name, Validators.required],
        sets: [ex.set, Validators.required],
        reps: [ex.rep, Validators.required],
        weight: [ex.weight, Validators.required],
      });
      this.exercises.push(group);
    });
  }

  deleteWorkout(index: number) {
    const workout = this.workouts[index];
    if (confirm('Are you sure you want to delete this workout?')) {
      this.adminWorkoutService.deleteWorkout(workout.id).subscribe({
        next: (res) => {
          alert(res);
          this.workouts.splice(index, 1);
        },
        error: (err) => alert('Error deleting workout: ' + err.error),
      });
    }
  }

  cancelForm() {
    this.resetForm();
  }

  resetForm() {
    this.showForm = false;
    this.showExerciseForm = false;
    this.isEditMode = false;
    this.isExerciseEditMode = false;
    this.editIndex = null;
    this.editExerciseId = null;
    this.workoutForm.reset();
    this.exercises.clear();
    this.exerciseForm.reset();
  }

  // ✅ Exercise Management
  openAddExerciseForm() {
    this.showExerciseForm = true;
    this.isExerciseEditMode = false;
    this.exerciseForm.reset();
  }

  editExercise(exercise: any) {
    this.showExerciseForm = true;
    this.isExerciseEditMode = true;
    this.editExerciseId = exercise.id;
    this.exerciseForm.patchValue({
      name: exercise.name,
    });
  }

  deleteExercise(id: number) {
    if (confirm('Are you sure you want to delete this exercise?')) {
      this.exerciseService.deleteExercise(id).subscribe({
        next: (res: any) => {
          alert(res);
          this.loadExercises();
        },
        error: (err) => alert('Error deleting exercise: ' + err.error),
      });
    }
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) this.exerciseForm.patchValue({ file });
  }

  submitExercise() {
    if (this.exerciseForm.invalid) {
      alert('Please provide name and image!');
      return;
    }

    const formData = new FormData();
    formData.append('name', this.exerciseForm.value.name);
    if (this.exerciseForm.value.file)
      formData.append('file', this.exerciseForm.value.file);

    if (this.isExerciseEditMode && this.editExerciseId !== null) {
      this.exerciseService
        .updateExercise(this.editExerciseId, formData)
        .subscribe({
          next: (res: any) => {
            alert(res);
            this.loadExercises();
            this.resetForm();
          },
          error: (err) => alert('Error updating exercise: ' + err.error),
        });
    } else {
      this.exerciseService.uploadExercise(formData).subscribe({
        next: (res: any) => {
          alert(res);
          this.loadExercises();
          this.resetForm();
        },
        error: (err) => alert('Error uploading exercise: ' + err.error),
      });
    }
  }
}
