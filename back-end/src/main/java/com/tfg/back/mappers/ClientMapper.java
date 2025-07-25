package com.tfg.back.mappers;

import com.tfg.back.enums.UserStatus;
import com.tfg.back.model.Client;
import com.tfg.back.model.dtos.client.ClientDetailsDto;
import com.tfg.back.model.dtos.client.ClientDtoCreate;
import com.tfg.back.model.dtos.client.ClientDtoGet;
import com.tfg.back.model.dtos.client.ClientDtoUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ClientMapper {

    private final PasswordEncoder passwordEncoder;
    private final AppointmentMapper appointmentMapper;
    private final MedicalPrescriptionMapper medicalPrescriptionMapper;
    private final LabResultMapper labResultMapper;

    @Autowired
    public ClientMapper(PasswordEncoder passwordEncoder, AppointmentMapper appointmentMapper, MedicalPrescriptionMapper medicalPrescriptionMapper, LabResultMapper labResultMapper) {
        this.passwordEncoder = passwordEncoder;
        this.appointmentMapper = appointmentMapper;
        this.medicalPrescriptionMapper = medicalPrescriptionMapper;
        this.labResultMapper = labResultMapper;
    }

    public Client toEntity(ClientDtoCreate dto) {
        Objects.requireNonNull(dto, "Client DTO cannot be null");

        Client client = new Client();

        client.setFullName(dto.fullName());
        client.setEmail(dto.email());
        client.setAddress(dto.address());
        client.setHashedPassword(passwordEncoder.encode(dto.password()));
        client.setPhoneNumber(dto.phoneNumber());
        client.setDateOfBirth(dto.dateOfBirth());
        client.setBloodType(dto.bloodType());

        client.setStatus(UserStatus.ACTIVE);
        client.setCreatedAt(LocalDateTime.now());

        client.setMembershipLevel(dto.membershipLevel());
        client.setEmergencyContact(dto.emergencyContact());

        client.setAppointments(new ArrayList<>());
        return client;
    }

    public Client updateEntity(Client client, ClientDtoUpdate dto) {
        Objects.requireNonNull(dto, "Client DTO cannot be null");

        client.setFullName(dto.fullName());
        client.setEmail(dto.email());
        client.setPhoneNumber(dto.phoneNumber());
        client.setDateOfBirth(dto.dateOfBirth());
        client.setAddress(dto.address());
        client.setStatus(UserStatus.ACTIVE);
        client.setBloodType(dto.bloodType());

        client.setMembershipLevel(dto.membershipLevel());
        client.setEmergencyContact(dto.emergencyContact());

        client.setUpdatedAt(LocalDateTime.now());

        return client;
    }

    public ClientDtoGet toGetDto(Client client) {

        return ClientDtoGet.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .email(client.getEmail())
                .bloodType(client.getBloodType())
                .phoneNumber(client.getPhoneNumber())
                .dateOfBirth(client.getDateOfBirth())
                .membershipLevel(client.getMembershipLevel())
                .address(client.getAddress())
                .emergencyContact(client.getEmergencyContact())
                .createdAt(client.getCreatedAt())
                .build();
    }

    public ClientDetailsDto toDetailsDto(Client client) {
        return ClientDetailsDto.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .dateOfBirth(client.getDateOfBirth())
                .status(client.getStatus())
                .address(client.getAddress())
                .membershipLevel(client.getMembershipLevel())
                .emergencyContact(client.getEmergencyContact())
                .bloodType(client.getBloodType())
                .appointments(appointmentMapper.toDtoGetList(client.getAppointments()))
                .prescriptionsReceived(medicalPrescriptionMapper.toDtoGetList(client.getPrescriptionsReceived()))
                .labResultsReceived(labResultMapper.toDtoGetList(client.getLabResultsReceived()))
                .build();
    }

    public List<ClientDtoGet> toGetDtoList(List<Client> clients) {
        return clients.stream().map(this::toGetDto).toList();
    }
}

