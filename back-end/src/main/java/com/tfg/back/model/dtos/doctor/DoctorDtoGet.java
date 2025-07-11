package com.tfg.back.model.dtos.doctor;


import com.tfg.back.enums.Specialization;
import com.tfg.back.model.Department;
import com.tfg.back.model.WorkingHours;
import com.tfg.back.model.dtos.appointment.AppointmentDtoGet;
import com.tfg.back.model.dtos.feedBack.FeedBackDtoGet;
import com.tfg.back.model.dtos.medicalPrescription.MedicalPrescriptionDtoGet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDtoGet {

    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private String medicalLicenseNumber;
    private Department department;
    private Specialization specialization;
    private String address;
    private Set<WorkingHours> workingHours;
}
