package com.tfg.back.service;

import com.tfg.back.model.Client;
import com.tfg.back.model.Notification;
import com.tfg.back.model.dtos.appointment.AppointmentDtoGet;
import com.tfg.back.model.dtos.client.ClientDtoCreate;
import com.tfg.back.model.dtos.client.ClientDtoGet;
import com.tfg.back.model.dtos.client.ClientDtoUpdate;
import com.tfg.back.utils.ChangePasswordRequest;

import java.util.List;

public interface ClientService {
    ClientDtoGet createClient(ClientDtoCreate dto);
    ClientDtoGet getClientById(Long id);
    ClientDtoGet getClientByEmail(String email);
    List<ClientDtoGet> getAllClients();
    ClientDtoGet updateClient(Long id, ClientDtoUpdate dto);
    void deleteClient(String email);
    List<AppointmentDtoGet> getAppointmentsByClientEmail(String email);
    void inactivateClient(String email);
    List<Notification> getClientsNotifications(String email);
    Client findClientByEmail(String email);

    void changePassword(String email, ChangePasswordRequest newPassword);

    void activateClient(String email);

    void suspendClient(String email);
}
