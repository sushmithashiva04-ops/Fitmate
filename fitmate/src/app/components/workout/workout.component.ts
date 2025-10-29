import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-workout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './workout.component.html',
  styleUrls: ['./workout.component.css'],
})
export class WorkoutComponent implements OnInit {
  showAddForm = false;
  isEditMode = false;
  editWorkoutId: number | null = null;

  // 🔹 Floating modal controls
  showCaloriePopup = false;
  selectedWorkoutId: number | null = null;
  enteredCalories: string = '';

  staticExercises: string[] = [
    'Squat',
    'Push-up',
    'Lunges',
    'Plank',
    'Deadlift',
    'Bench Press',
    'Pull-up',
    'Bicep Curl',
    'Triceps Dip',
    'Mountain Climber',
  ];

  newWorkout: any = {
    type: '',
    name: '',
    duration: '',
    scheduledDate: '',
    exercises: [],
  };

  todayWorkouts: any[] = [];
  adminWorkouts: any[] = [];
  userWorkouts: any[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadWorkouts();
  }

  // ✅ Load all workout sections
  loadWorkouts() {
    this.authService.getTodayWorkouts().subscribe((data) => {
      this.todayWorkouts = data;
    });

    this.authService.getAdminWorkouts().subscribe((data) => {
      this.adminWorkouts = data;
    });

    this.authService.getUserWorkouts().subscribe((data) => {
      this.userWorkouts = data;
    });
  }

  // ✅ Add new exercise block in form
  addExerciseBlock() {
    this.newWorkout.exercises.push({
      exerciseName: '',
      sets: '',
      reps: '',
      weight: '',
    });
  }

  // ✅ Remove exercise block
  removeExerciseBlock(index: number) {
    this.newWorkout.exercises.splice(index, 1);
  }

  // ✅ Open form for Add
  openAddForm() {
    this.showAddForm = true;
    this.isEditMode = false;
    this.resetForm();
  }

  // ✅ Cancel and hide form
  cancelForm() {
    this.showAddForm = false;
    this.isEditMode = false;
  }

  // ✅ Save new or edited workout
  saveWorkout() {
    if (this.newWorkout.exercises.length === 0) {
      alert('Add at least one exercise before saving.');
      return;
    }

    if (this.isEditMode && this.editWorkoutId !== null) {
      this.authService
        .updateWorkout(this.editWorkoutId, this.newWorkout)
        .subscribe(() => {
          this.loadWorkouts();
          this.cancelForm();
        });
    } else {
      this.authService.addWorkout(this.newWorkout).subscribe(() => {
        this.loadWorkouts();
        this.cancelForm();
      });
    }
  }

  // ✅ Edit existing workout
  editWorkout(workout: any) {
    this.isEditMode = true;
    this.editWorkoutId = workout.id;
    this.newWorkout = JSON.parse(JSON.stringify(workout));
    this.showAddForm = true;
  }

  // ✅ Delete a workout
  deleteWorkout(id: number) {
    if (confirm('Are you sure you want to delete this workout?')) {
      this.authService.deleteWorkout(id).subscribe(() => {
        this.loadWorkouts();
      });
    }
  }

  // ✅ Reschedule (Admin workout -> Add to today)
  rescheduleWorkout(workoutId: number) {
    this.authService.rescheduleWorkout(workoutId).subscribe(() => {
      this.loadWorkouts();
    });
  }

  // ✅ Mark workout complete (opens floating popup)
  markAsComplete(workoutId: number) {
    this.selectedWorkoutId = workoutId;
    this.enteredCalories = '';
    this.showCaloriePopup = true;
  }

  confirmCalories() {
    if (!this.calorieInput || !this.selectedWorkout) return;

    console.log('➡️ Sending calorie data to backend:', {
      workoutId: this.selectedWorkout.id,
      calories: this.calorieInput,
    });

    this.authService
      .markWorkoutComplete(this.selectedWorkout.id, +this.calorieInput)
      .subscribe({
        next: (response) => {
          console.log('✅ Backend responded:', response);

          // 🔹 Immediately mark as completed in the visible list
          const target = this.todayWorkouts.find(
            (w) => w.id === this.selectedWorkout.id
          );
          if (target) target.completed = true;

          // 🔹 Close popup without reloading the entire list
          this.showCaloriePopup = false;
          this.selectedWorkout = null;
          this.calorieInput = null;

          alert('Workout marked as complete!');
        },
        error: (err) => {
          console.error('❌ Error while marking complete:', err);
        },
      });
  }

  // ✅ Cancel calorie popup
  closeCaloriePopup() {
    this.showCaloriePopup = false;
    this.selectedWorkoutId = null;
    this.enteredCalories = '';
  }

  // ✅ Remove workout from today’s session
  removeWorkout(id: number) {
    if (confirm('Remove this workout from today?')) {
      this.authService.deleteWorkout(id).subscribe(() => {
        this.loadWorkouts();
      });
    }
  }

  selectedWorkout: any = null;
  calorieInput: number | null = null;

  // 🔹 Open the popup
  openCaloriePopup(workout: any) {
    this.selectedWorkout = workout;
    this.showCaloriePopup = true;
    this.calorieInput = null;
  }

  // 🔹 Confirm calories (and disable "Mark Complete")

  // ✅ Helper for image display
  getExerciseImage(imageUrl: string): string {
    if (!imageUrl || imageUrl === 'No image available') {
      return 'assets/no-image.png';
    }
    return imageUrl;
  }

  // ✅ Reset form
  resetForm() {
    this.newWorkout = {
      type: '',
      name: '',
      duration: '',
      scheduledDate: '',
      exercises: [],
    };
  }
}
