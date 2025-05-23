package com.tfg.back.controller;

import com.tfg.back.model.Appointment;
import com.tfg.back.model.dtos.appointment.AppointmentCreateDto;
import com.tfg.back.model.dtos.appointment.AppointmentDtoGet;
import com.tfg.back.service.AppointmentService;
import com.tfg.back.utils.BookAppointmentRequest;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDtoGet>> getAllAppointments() {
        List<AppointmentDtoGet> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDtoGet> getAllAppointments(@PathVariable Long id) {
        AppointmentDtoGet appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/")
    public ResponseEntity<AppointmentDtoGet> createAppointment(@Valid @RequestBody AppointmentCreateDto appointmentDto, Authentication authentication) {
        String clientEmail = authentication.getName();
        AppointmentDtoGet appointment = appointmentService.createAppointment(appointmentDto, clientEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        String clientEmail = authentication.getName();
        appointmentService.cancelAppointment(id, clientEmail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id, Authentication authentication) {
        String clientEmail = authentication.getName();
        appointmentService.confirmAppointment(id, clientEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/book-appointment")
    public ResponseEntity<AppointmentDtoGet> bookAppointment(@RequestBody BookAppointmentRequest request){
        return ResponseEntity.ok(appointmentService.bookAppointment(request.doctorId(), request.date(), request.startTime(), request.clientId(), request.type(),request.reason()));
    }

    /*
    @GetMapping("/available")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LocalDateTime> slots = appointmentService.getAvailableSlots(doctorId, date);
        if (slots.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(slots);
    }
     */
}
