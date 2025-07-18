package com.tfg.back.controller;

import static com.tfg.back.constants.BaseRoutes.*;

import com.tfg.back.model.User;
import com.tfg.back.model.dtos.auth.AuthRequest;
import com.tfg.back.model.dtos.client.*;
import com.tfg.back.service.ClientService;
import com.tfg.back.model.dtos.users.ChangePasswordRequest;
import com.tfg.back.service.impl.LoginService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(CLIENT)
@Validated
public class ClientController {

    private final ClientService clientService;
    private final LoginService loginService;

    @Autowired
    public ClientController(ClientService clientService, LoginService loginService){
        this.clientService = clientService;
        this.loginService = loginService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody ClientDtoCreate client) {
        ClientDtoGet createdClient = clientService.createClient(client);
        AuthRequest request = new AuthRequest(client.email(), client.password(), false);
        return loginService.login(request);
    }

    @GetMapping("/clients")
    public ResponseEntity<Page<ClientDtoGet>> getClients(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<ClientDtoGet> clients = clientService.findClients(search, PageRequest.of(page, size));
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<ClientSummaryResponse> getMySummary(@AuthenticationPrincipal User patient) {
        ClientSummaryResponse summary = clientService.findClientSummaryById(patient);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/my-name")
    public ResponseEntity<FullNameWrapper> getMyName(@AuthenticationPrincipal User patient) {
        String fullName = clientService.findClientFullName(patient);
        return ResponseEntity.ok(new FullNameWrapper(fullName));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ClientDetailsDto> getClientDetailsByDoctor(@PathVariable @NotNull UUID id) {
        ClientDetailsDto client = clientService.getClientDetailsByDoctor(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/profile")
    public ResponseEntity<ClientDtoGet> getMyProfile(@AuthenticationPrincipal User patient) {
        ClientDtoGet client = clientService.findClientById(patient);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User patient, @Valid @RequestBody ChangePasswordRequest newPassword) {
        clientService.changePassword(patient, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDtoGet> updateClient(@PathVariable UUID id,@Valid @RequestBody ClientDtoUpdate dto){
        ClientDtoGet client = clientService.updateClient(id, dto);
        return ResponseEntity.ok(client);
    }

    //NOTE: ACCOUNT STATUS METHODS
    @PutMapping("/inactivateAccount")
    public ResponseEntity<Void> inactivateAccount(@AuthenticationPrincipal User patient) {
        clientService.inactivateClient(patient);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate-account/{id}")
    public ResponseEntity<Void> activateAccount(@PathVariable UUID id) {
        clientService.activateClient(id);
        return ResponseEntity.noContent().build();
    }

    //note: ONLY THE ADMIN CAN OPERATE THESE METHODS
    @PutMapping("/suspend-account/{id}")
    public ResponseEntity<Void> suspendAccount(@PathVariable UUID id) {
        clientService.suspendClient(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-email/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
