package com.example.patientmanagement.controller;

import com.example.patientmanagement.dto.PatientRequestDto;
import com.example.patientmanagement.dto.PatientResponseDto;
import com.example.patientmanagement.entity.Patient;
import com.example.patientmanagement.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> create(@RequestBody @Valid PatientRequestDto dto) {
        PatientResponseDto created = service.create(dto);
        URI location = URI.create("/api/v1/patients/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }
}
