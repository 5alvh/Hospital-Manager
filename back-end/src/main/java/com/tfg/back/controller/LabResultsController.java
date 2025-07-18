package com.tfg.back.controller;

import static com.tfg.back.constants.BaseRoutes.*;

import com.tfg.back.model.User;
import com.tfg.back.model.dtos.labResults.LabResultDtoCreate;
import com.tfg.back.model.dtos.labResults.LabResultDtoGet;
import com.tfg.back.service.LabResultsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(LAB_RESULT)
public class LabResultsController {

    private final LabResultsService labResultService;

    @Autowired
    public LabResultsController(LabResultsService labResultService) {
        this.labResultService = labResultService;
    }

    @PostMapping
    public ResponseEntity<LabResultDtoGet> create(@RequestBody @Valid LabResultDtoCreate labResult, @AuthenticationPrincipal User doctor) {
        LabResultDtoGet labResultDtoGet = labResultService.createLabResult(labResult, doctor);
        return ResponseEntity.ok(labResultDtoGet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabResultDtoGet> getById(@PathVariable @Positive Long id) {
        LabResultDtoGet labResult = labResultService.findLabResultById(id);
        return ResponseEntity.ok(labResult);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<LabResultDtoGet>> getMyLabResults(@AuthenticationPrincipal User patient,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Page<LabResultDtoGet> labResults = labResultService.findLabResultsByPatientId(patient, PageRequest.of(page, size));
        return ResponseEntity.ok(labResults);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labResultService.deleteLabResult(id);
        return ResponseEntity.noContent().build();
    }
}
