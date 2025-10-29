import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { HeaderComponent } from '../shared/header/header.component';
import { FooterComponent } from '../shared/footer/footer.component';
import { AuthService } from '../../services/auth.service';
import { Profile } from '../../models/profile';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HeaderComponent,
    FooterComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  profile: Profile | null = null;
  isEditing = false;
  profileForm!: FormGroup;
  message = '';

  constructor(
    private profileService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
    this.initForm();
  }

  initForm() {
    this.profileForm = this.fb.group({
      age: ['', Validators.required],
      gender: ['', Validators.required],
      height: ['', Validators.required],
      weight: ['', Validators.required],
      fitnessLevel: ['', Validators.required],
    });
  }

  loadProfile() {
    this.profileService.getProfile().subscribe({
      next: (data: any) => {
        this.profile = data;
      },
      error: () => {
        this.profile = null; // no profile found
      },
    });
  }

  onAdd() {
    this.isEditing = true;
  }

  onEdit() {
    this.isEditing = true;
    this.profileForm.patchValue(this.profile!);
  }

  onSubmit() {
    if (this.profileForm.invalid) return;

    const details = this.profileForm.value;

    const req = this.profile
      ? this.profileService.updateProfile(details)
      : this.profileService.addProfile(details);

    req.subscribe({
      next: (res: string) => {
        const expectedMessages = [
          'Profile details added successfully',
          'Profile details updated successfully',
        ];

        if (expectedMessages.includes(res)) {
          this.message = res; // ✅ show backend message directly
        } else {
          this.message = 'Failed to save profile details'; // ❌ unexpected response
        }

        this.isEditing = false;
        this.loadProfile(); // reload the updated details
      },
      error: (err) => {
        console.error('Profile save error:', err);
        this.message = err.error || 'Failed to save profile details';
      },
    });
  }
}
