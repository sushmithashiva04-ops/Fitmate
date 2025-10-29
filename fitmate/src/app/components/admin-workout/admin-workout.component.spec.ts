import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminWorkoutComponent } from './admin-workout.component';

describe('AdminWorkoutComponent', () => {
  let component: AdminWorkoutComponent;
  let fixture: ComponentFixture<AdminWorkoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminWorkoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminWorkoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
