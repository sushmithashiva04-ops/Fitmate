import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardService } from '../../services/admin-dash-board.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  totalWorkouts: number | null = null;
  scheduledWorkouts: number | null = null;
  completionRate: number | null = null;

  loading = true;
  errorMessage = '';

  constructor(private dashboardService: AdminDashboardService) {}

  ngOnInit(): void {
    this.loadMetrics();
  }

  loadMetrics() {
    this.loading = true;
    this.errorMessage = '';

    Promise.all([
      this.dashboardService.getTotalWorkouts().toPromise(),
      this.dashboardService.getScheduledWorkouts().toPromise(),
      this.dashboardService.getCompletionRate().toPromise(),
    ])
      .then(([total, scheduled, rate]) => {
        this.totalWorkouts = total ?? 0;
        this.scheduledWorkouts = scheduled ?? 0;
        this.completionRate = rate ?? 0;
      })
      .catch(() => {
        this.errorMessage = 'Failed to load metrics';
      })
      .finally(() => {
        this.loading = false;
      });
  }
}
