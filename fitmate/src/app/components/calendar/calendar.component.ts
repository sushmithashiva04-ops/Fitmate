import { Component, OnInit } from '@angular/core';
import { CalendarOptions } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { FullCalendarModule } from '@fullcalendar/angular';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [FullCalendarModule, CommonModule],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css'],
})
export class CalendarComponent implements OnInit {
  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    plugins: [dayGridPlugin, interactionPlugin],
    events: [],
    eventClick: this.onEventClick.bind(this),
    height: '80vh',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: '',
    },
  };

  constructor(private calendarService: AuthService) {}

  ngOnInit(): void {
    this.loadUserEvents();
  }

  loadUserEvents() {
    this.calendarService.getUserEvents().subscribe({
      next: (data) => {
        console.log('✅ Backend events received:', data);

        this.calendarOptions.events = data.map((event: any) => ({
          title: event.title,
          start: event.start,
          color: event.color || '#00C853',
        }));

        console.log(
          '🎨 Processed events for calendar:',
          this.calendarOptions.events
        );
      },
      error: (err) => {
        console.error('❌ Error loading events:', err);
      },
    });
  }

  onEventClick(info: any) {
    alert(`Workout: ${info.event.title}\nDate: ${info.event.startStr}`);
  }
}
