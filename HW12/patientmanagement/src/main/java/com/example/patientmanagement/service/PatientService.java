package com.example.patientmanagement.service;

import com.example.patientmanagement.dto.PatientRequestDto;
import com.example.patientmanagement.dto.PatientResponseDto;
import com.example.patientmanagement.entity.Patient;
import com.example.patientmanagement.repository.PatientRepository;
import com.example.patientmanagement.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repo;

    public PatientResponseDto getById(Long id) {
        Patient patient = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
        return toResponseDto(patient);  // ← return DTO not Entity
    }

    @Transactional
    public PatientResponseDto create(PatientRequestDto dto) {
        Patient patient = toEntity(dto);  // turn DTO to Entity
        return toResponseDto(repo.save(patient));
    }

    // Mapper
    private PatientResponseDto toResponseDto(Patient p) {
        return new PatientResponseDto(p.getId(), p.getName(), p.getEmail());
    }

    private Patient toEntity(PatientRequestDto dto) {
        Patient p = new Patient();
        p.setName(dto.getName());
        p.setEmail(dto.getEmail());
        return p;
    }
}
