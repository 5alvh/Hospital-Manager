import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DatePipe, NgClass, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { BloodType } from '../../models/enums/blood-type';
import { AuthService } from '../../services/auth/auth.service';
import { ClientDtoCreate } from '../../models/client-dto-create';
import { LocalStorageManagerService } from '../../services/auth/local-storage-manager.service';
enum MembershipLevel {
  BASIC = 'BASIC',
  PREMIUM = 'PREMIUM',
  VIP = 'VIP',
  INSURANCE_COVERED = 'INSURANCE_COVERED'
}

@Component({
  selector: 'app-client-signup',
  templateUrl: './client-signup.component.html',
  styleUrls: ['./client-signup.component.scss'],
  imports: [NgIf, ReactiveFormsModule, NgClass, DatePipe, TitleCasePipe, RouterLink, NgFor],
  providers: [DatePipe, TitleCasePipe]
})
export class ClientSignupComponent implements OnInit {
  signupForm!: FormGroup;
  membershipLevels = Object.values(MembershipLevel);
  maxDate = new Date(); // Today's date as max date
  submitted = false;
  processing = false;
  signupSuccess = false;
  passwordVisible = false;
  confirmPasswordVisible = false;
  includeEmergencyContact = false;
  bloodTypes = Object.values(BloodType);

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private localStorageManager: LocalStorageManagerService
  ) { }

  ngOnInit(): void {

    const token = this.localStorageManager.getToken();
    if (token) {
      this.router.navigate(['/dashboard-client']);
      return;
    }

    this.initForm();

    // Watch for includeEmergencyContact changes
    this.signupForm.get('includeEmergencyContact')?.valueChanges.subscribe(include => {
      const emergencyContactGroup = this.signupForm.get('emergencyContact');

      if (include) {
        emergencyContactGroup?.setValidators([Validators.required]);
        emergencyContactGroup?.get('contactName')?.setValidators([Validators.required, Validators.maxLength(100)]);
        emergencyContactGroup?.get('contactPhone')?.setValidators([
          Validators.required,
          Validators.pattern('^\\+?[0-9\\-\\s()]*$')
        ]);
      } else {
        emergencyContactGroup?.clearValidators();
        emergencyContactGroup?.get('contactName')?.clearValidators();
        emergencyContactGroup?.get('contactPhone')?.clearValidators();
        emergencyContactGroup?.get('relationship')?.clearValidators();
      }

      emergencyContactGroup?.updateValueAndValidity();
      emergencyContactGroup?.get('contactName')?.updateValueAndValidity();
      emergencyContactGroup?.get('contactPhone')?.updateValueAndValidity();
      emergencyContactGroup?.get('relationship')?.updateValueAndValidity();
    });
  }

  initForm(): void {
    this.signupForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      passwordConfirmation: ['', [Validators.required]],
      phoneNumber: ['', [Validators.pattern('^\\+?[0-9\\-\\s()]*$')]],
      dateOfBirth: [null, [Validators.required]],
      address: ['', [Validators.maxLength(200)]], // NEW
      bloodType: [null, Validators.required],
      membershipLevel: [MembershipLevel.BASIC],
      includeEmergencyContact: [false],
      emergencyContact: this.fb.group({
        contactName: [''],
        contactPhone: [''],
        relationship: ['', [Validators.maxLength(50)]]
      })
    }, {
      validators: this.passwordMatchValidator
    });
  }

  // Custom validator to check if password and confirmation match
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('passwordConfirmation');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ ...confirmPassword.errors, passwordMismatch: true });
      return { passwordMismatch: true };
    }

    if (confirmPassword?.hasError('passwordMismatch')) {
      const errors = { ...confirmPassword.errors };
      delete errors['passwordMismatch'];
      confirmPassword.setErrors(Object.keys(errors).length ? errors : null);
    }

    return null;
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.confirmPasswordVisible = !this.confirmPasswordVisible;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.signupForm.invalid) {
      const firstElementWithError = document.querySelector('.ng-invalid');
      if (firstElementWithError) {
        firstElementWithError.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return;
    }

    this.processing = true;

    const formValue = this.signupForm.value;
    const clientData: ClientDtoCreate = {
      fullName: formValue.fullName,
      email: formValue.email,
      password: formValue.password,
      passwordConfirmation: formValue.passwordConfirmation,
      phoneNumber: formValue.phoneNumber,
      dateOfBirth: formValue.dateOfBirth,
      membershipLevel: formValue.membershipLevel,
      address: formValue.address, // NEW
      bloodType: formValue.bloodType,
      emergencyContact: formValue.includeEmergencyContact ? formValue.emergencyContact : null
    };

    this.authService.registerClient(clientData).subscribe({
      next: response => {
        setTimeout(() => {
          console.log('Client signup data:', clientData);
          this.processing = false;
          this.signupSuccess = true;
          const role = response.role;
          this.localStorageManager.setToken(response.token);
          this.localStorageManager.setUserData(role);
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 3000);
        }, 1500);
      },
      error: error => {
        console.error('Error registering client:', error);
        this.processing = false;
      }
    })
  }

  // Helper getters for form controls
  get f() { return this.signupForm.controls; }
  get ec() { return (this.signupForm.get('emergencyContact') as FormGroup).controls; }
}