package com.tfg.back.mappers;

import com.tfg.back.enums.AppointmentStatus;
import com.tfg.back.enums.AppointmentType;
import com.tfg.back.enums.SearchType;
import com.tfg.back.exceptions.user.UserNotFoundException;
import com.tfg.back.model.*;
import com.tfg.back.model.dtos.appointment.AppointmentCreateDto;
import com.tfg.back.model.dtos.appointment.AppointmentDtoGet;
import com.tfg.back.repository.ClientRepository;
import com.tfg.back.repository.DoctorRepository;
import com.tfg.back.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class AppointmentMapper {

    private final ClientRepository clientRepository;
    private final DoctorRepository doctorService;
    private final NotificationRepository notificationRepository;

    public Appointment toEntity(AppointmentCreateDto dto, String email) {
        Client client = findClientByEmail(email);
        Doctor doctor = doctorService.findByEmail(dto.getDoctorEmail()).get();
        Department department = doctor.getDepartment();
        createAppointmentNotification(client, dto.getAppointmentDateTime());
        return Appointment.builder()
                .client(client)
                .doctor(doctor)
                .department(department)
                .appointmentDateTime(dto.getAppointmentDateTime())
                .reason(dto.getReason())
                .status(AppointmentStatus.SCHEDULED)
                .type(AppointmentType.IN_PERSON)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public AppointmentDtoGet toDtoGet(Appointment appointment) {
        return AppointmentDtoGet.builder()
                .id(appointment.getId())
                .clientFullName(appointment.getClient().getFullName())
                .doctorFullName(appointment.getDoctor().getFullName())
                .reason(appointment.getReason())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .departmentName(appointment.getDepartment().getName())
                .build();
    }

    public List<AppointmentDtoGet> toDtoGetList(List<Appointment> appointments) {
        return appointments.stream().map(this::toDtoGet).toList();
    }

    private Client findClientByEmail(String email) {
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("Client email mustn't be null");
        }
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email, SearchType.EMAIL));
    }

    private void createAppointmentNotification(User client, LocalDateTime date) {
        Notification notification = new Notification();
        notification.setTitle("New Appointment");
        notification.setMessage("You have a new appointment:" + date);
        notification.setType("APPOINTMENT");
        notification.setSeen(false);
        notification.setDate(LocalDateTime.now());
        notification.setUser(client);
        notificationRepository.save(notification);
    }
}
