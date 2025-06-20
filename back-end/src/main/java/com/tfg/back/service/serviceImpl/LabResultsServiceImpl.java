package com.tfg.back.service.serviceImpl;

import com.tfg.back.exceptions.labResult.LabResultNotFoundException;
import com.tfg.back.exceptions.user.UnauthorizedToPerformThisAction;
import com.tfg.back.mappers.LabResultMapper;
import com.tfg.back.model.Client;
import com.tfg.back.model.Doctor;
import com.tfg.back.model.LabResult;
import com.tfg.back.model.Notification;
import com.tfg.back.model.dtos.labResults.LabResultDtoCreate;
import com.tfg.back.model.dtos.labResults.LabResultDtoGet;
import com.tfg.back.repository.LabResultsRepository;
import com.tfg.back.repository.NotificationRepository;
import com.tfg.back.service.ClientService;
import com.tfg.back.service.DoctorService;
import com.tfg.back.service.LabResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class LabResultsServiceImpl implements LabResultsService {

    private final LabResultsRepository labResultRepository;
    private final NotificationRepository notificationRepository;
    private final DoctorService doctorService;
    private final ClientService clientService;
    private final LabResultMapper labResultMapper;

    @Autowired
    public LabResultsServiceImpl(LabResultsRepository labResultRepository, NotificationRepository notificationRepository, DoctorService doctorService, ClientService clientService, LabResultMapper labResultMapper) {
        this.labResultRepository = labResultRepository;
        this.notificationRepository = notificationRepository;
        this.doctorService = doctorService;
        this.clientService = clientService;
        this.labResultMapper = labResultMapper;
    }

    @Override
    public List<LabResultDtoGet> getLabResultsByEmail(UUID email) {
        List<LabResult> labresults = labResultRepository.findByPatientId(email);
        return labResultMapper.toDtoGetList(labresults);
    }

    @Override
    public LabResultDtoGet sendLabResult(LabResultDtoCreate labResult, String doctorEmail) {
        Client client = clientService.findClientByEmail(labResult.getPatientEmail());
        Doctor doctor = doctorService.findDoctorByEmail(doctorEmail);
        LabResult labResultEntity = labResultMapper.toEntity(labResult, doctor, client);
        createNotification(client);
        return labResultMapper.toDtoGet(labResultRepository.save(labResultEntity));
    }

    @Override
    public LabResultDtoGet getLabResultById(Long id) {
        return labResultMapper.toDtoGet(findLabResultById(id));
    }

    @Override
    public void deleteLabResult(Long id, String email) {
        LabResult result = findLabResultById(id);
        if (!Objects.equals(result.getOrderedBy().getEmail(), email)) throw new UnauthorizedToPerformThisAction("Client with email: "+email+" is not authorized to perform this action");
        labResultRepository.delete(result);
    }

    private LabResult findLabResultById(Long id) {
        return  labResultRepository.findById(id)
                .orElseThrow(()-> new LabResultNotFoundException("Lab Result with ID: "+id+" is not found"));
    }

    //TODO: add notification service
    private void createNotification(Client client) {
        Notification notification = new Notification();
        notification.setTitle("New Lab Result");
        notification.setMessage("You have a new lab result");
        notification.setType("LAB_RESULT");
        notification.setSeen(false);
        notification.setDate(LocalDateTime.now());
        notification.setUser(client);
        notificationRepository.save(notification);
    }

    @Override
    public LabResult updateLabResult(Long id, LabResultDtoCreate updatedResult) {
        return null;
    }
}
