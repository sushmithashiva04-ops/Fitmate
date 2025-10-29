import { Component, OnInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { DashboardService } from '../../services/dash-board.service';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  // Backend data
  workoutTypes: any = {};
  weeklyStats: any = {};
  totalCalories: number = 0;
  totalDuration: number = 0;
  streak: number = 0;
  workoutCounts: any = { DONE: 0, PENDING: 0, SCHEDULED: 0 };

  // UI state
  selectedStatus: 'DONE' | 'PENDING' | 'SCHEDULED' = 'DONE';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData() {
    // 1️⃣ Workout type distribution (Pie)
    this.dashboardService.getWorkoutTypes().subscribe((data: any) => {
      this.workoutTypes = data;
      this.renderPieChart(data);
    });

    // 2️⃣ Weekly stats (Line)
    this.dashboardService.getWeeklyStats().subscribe((data: any) => {
      this.weeklyStats = data;
      this.renderLineChart(data);
    });

    // 3️⃣ Total calories
    this.dashboardService.getTotalCalories().subscribe((data: number) => {
      this.totalCalories = data;
    });

    // 4️⃣ Total duration
    this.dashboardService.getTotalDuration().subscribe((data: number) => {
      this.totalDuration = data;
    });

    // 5️⃣ Streak
    this.dashboardService.getStreak().subscribe((data: number) => {
      this.streak = data;
    });

    // 6️⃣ Counts (DONE, PENDING, SCHEDULED)
    this.dashboardService.getWorkoutCounts().subscribe((data: any) => {
      this.workoutCounts = data;
    });
  }

  renderPieChart(data: any) {
    const labels = Object.keys(data);
    const values = Object.values(data);

    new Chart('pieChart', {
      type: 'pie',
      data: {
        labels,
        datasets: [
          {
            label: 'Workout Type Distribution',
            data: values,
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'],
          },
        ],
      },
      options: {
        plugins: {
          legend: {
            position: 'bottom',
            labels: { color: '#fff' },
          },
        },
        responsive: true,
      },
    });
  }

  renderLineChart(data: any) {
    const labels = data.dates;
    const calories = data.calories;
    const durations = data.durations;

    new Chart('lineChart', {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Calories Burned',
            data: calories,
            borderColor: '#FF6384',
            backgroundColor: 'rgba(255,99,132,0.2)',
            fill: true,
            tension: 0.3,
          },
          {
            label: 'Duration (mins)',
            data: durations,
            borderColor: '#36A2EB',
            backgroundColor: 'rgba(54,162,235,0.2)',
            fill: true,
            tension: 0.3,
          },
        ],
      },
      options: {
        scales: {
          x: { ticks: { color: '#fff' } },
          y: { ticks: { color: '#fff' } },
        },
        plugins: {
          legend: { labels: { color: '#fff' } },
        },
        responsive: true,
      },
    });
  }

  // 🧭 Switch between DONE / PENDING / SCHEDULED
  setSelectedStatus(status: 'DONE' | 'PENDING' | 'SCHEDULED') {
    this.selectedStatus = status;
  }
}
