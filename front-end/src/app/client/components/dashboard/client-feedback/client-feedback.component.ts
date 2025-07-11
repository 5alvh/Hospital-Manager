import { DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VisitedDoctorDto } from '../dashboard-client.component';
import Swal from 'sweetalert2';
import { ClientLoadingWrapperComponent } from "../client-loading-wrapper/client-loading-wrapper.component";
import { Feedback } from '../../../../doctor/doctor-dashboard/doctor-dashboard.component';
import { DoctorService } from '../../../../services/doctor-services/doctor.service';
import { FeedbackService } from '../../../../services/feedback-service/feedback.service';

@Component({
  selector: 'app-client-feedback',
  imports: [FormsModule, NgFor, NgIf, NgClass, DatePipe, ClientLoadingWrapperComponent],
  templateUrl: './client-feedback.component.html',
  styleUrl: './client-feedback.component.scss'
})
export class ClientFeedbackComponent implements OnInit {

  @Input({ required: true }) patientId!: number;
  feedbacks!: Feedback[];
  selectedRating: number = 0;
  doctorFeedbackId: number | null = null;
  feedbackDoctors: VisitedDoctorDto[] = [];
  isGeneralFeedback: boolean = true;
  selectedFeedbackType: string = 'general';
  feedbackMessage: string = '';
  hoveredRating: number = 0;
  isLoading: boolean = true;
  selectedFeedback?:Feedback;
  constructor(private doctorService: DoctorService, private feedbackService: FeedbackService) { }
  ngOnInit(): void {
    this.feedbackService.getAllFeedBacks().subscribe(
      (response) => {
        this.feedbacks = response;
        this.isLoading = false;
      }
    )
  }

  setRating(rating: number): void {
    this.selectedRating = rating;
  }
  onFeedbackTypeChange() {
    this.isGeneralFeedback = !(this.selectedFeedbackType === 'doctor');
    if (this.selectedFeedbackType === 'doctor') {
      this.doctorService.getVisitedDoctorByClientId(this.patientId).subscribe({
        next: (doctors) => {
          this.feedbackDoctors = doctors;
        },
        error: (error) => {
          console.error('Error fetching visited doctors:', error);
        }
      });
    }
  }

  cancelUpdate(){
    this.selectedFeedback =undefined;
  }
  setHover(rating: number): void {
    this.hoveredRating = rating;
  }

  clearHover(): void {
    this.hoveredRating = 0;
  }

  submitFeedback(): void {
    if (this.selectedRating === 0) {
      Swal.fire('Please select a rating before submitting.');
      return;
    }

    if (this.feedbackMessage.trim() === '' || this.feedbackMessage.length < 5) {
      Swal.fire('Please enter a feedback message before submitting.');
      return;
    }
    const feedback = {
      comment: this.feedbackMessage.trim(),
      rating: this.selectedRating,
      writtenToId: this.isGeneralFeedback ? null : this.doctorFeedbackId,
      type: this.isGeneralFeedback ? 'GENERAL' : 'DOCTOR'
    };
    this.feedbackService.submitFeedback(feedback).subscribe({
      next: () => {
        Swal.fire('Thank you for your feedback!');
        this.feedbacks.push({
          id: 0,
          patientName: '',
          rating: feedback.rating,
          createdAt: new Date(),
          comment: this.feedbackMessage,
          type:this.selectedFeedbackType
        });
        this.feedbackMessage = '';
      },
      error: () => {
        Swal.fire('There was an error submitting your feedback. Please try again later.');
      }
    });


    this.selectedRating = 0;
    this.hoveredRating = 0;
    this.selectedFeedbackType = '';
  }

  editFeedback(arg0: number) {
    throw new Error('Method not implemented.');
  }
  deleteFeedback(feebackId: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: "Know that if you delete your feedback.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, keep it'
    }).then((result) => {
      if (result.isConfirmed) {
        this.feedbackService.deleteFeedback(feebackId).subscribe({
          next: () => {
            const index = this.feedbacks.findIndex(feedback => feedback.id === feebackId);
            if (index !== -1) {
              this.feedbacks.splice(index, 1);
            }
            Swal.fire(
              'Cancelled!',
              'Your feedback has been deleted.',
              'success'
            );
          },
          error: () => {
            Swal.fire(
              'Error!',
              'There was an issue deleting your appointment.',
              'error'
            );
          }
        });
      }
    });
  }
}
