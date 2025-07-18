package com.tfg.back.model.dtos.medicalPrescription;


import com.tfg.back.enums.PrescriptionStatus;
import com.tfg.back.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MedicalPrescriptionDtoCreate(

        @NotNull
        @Size(min = 1, message = "At least one medication is required")
        @Schema(description = "List of prescribed medications", required = true)
        Medication[] medications,

        @Schema(description = "Client UUID (used if searchType is ID)")
        UUID clientId,

        @NotNull
        @Schema(description = "Status of the prescription", example = "DRAFT or PUBLISHED", required = true)
        PrescriptionStatus status

) {}

