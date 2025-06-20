package com.tfg.back.service.serviceImpl;

import com.tfg.back.enums.SearchType;
import com.tfg.back.exceptions.notification.NotificationNotFoundException;
import com.tfg.back.exceptions.user.UnauthorizedToPerformThisAction;
import com.tfg.back.exceptions.user.UserNotFoundException;
import com.tfg.back.model.Client;
import com.tfg.back.model.Notification;
import com.tfg.back.repository.ClientRepository;
import com.tfg.back.repository.NotificationRepository;
import com.tfg.back.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClientRepository clientRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ClientRepository clientRepository) {
        this.notificationRepository = notificationRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Notification> getAllClientNotifications(UUID email) {
        List<Notification> notifications = notificationRepository.findByUserId(email);
        return notifications;
    }

    @Override
    public void seenNotification(Long id, UUID email) {
        Client client = clientRepository.findById(email).orElseThrow(()-> new UserNotFoundException(email, SearchType.EMAIL));
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(()-> new NotificationNotFoundException("Notification with ID: "+id+" is not found"));
        notification.setSeen(true);
        notificationRepository.save(notification);
    }
}
