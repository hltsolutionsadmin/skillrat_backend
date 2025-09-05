package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.InternshipOrJobDTO;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.services.SRExperienceService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experience")
public class SRExperienceController {

    @Resource(name = "srExperienceService")
    private SRExperienceService experienceService;

    // =========================================================
    // 🔹 Create / Save Experience
    // =========================================================
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addExperience(@Valid @RequestBody ExperienceDTO dto) {
        return experienceService.save(dto);
    }

    // =========================================================
    // 🔹 Experience Endpoints
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<List<ExperienceDTO>> getAllExperiencesForCurrentUser() {
        return experienceService.getAllExperiencesForCurrentUser();
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<ExperienceDTO> getExperienceById(@PathVariable Long experienceId) {
        return experienceService.getExperienceById(experienceId);
    }

    // =========================================================
    // 🔹 Education Endpoints
    // =========================================================

    @GetMapping("/educations")
    public ResponseEntity<List<EducationDTO>> getEducationsForCurrentUser() {
        return experienceService.getEducationsForCurrentUser();
    }

    @GetMapping("/educations/{educationId}")
    public ResponseEntity<EducationDTO> getEducationById(@PathVariable Long educationId) {
        return experienceService.getEducationById(educationId);
    }

    // =========================================================
    // 🔹 Internship / Job Endpoints
    // =========================================================

    @GetMapping("/internships-jobs")
    public ResponseEntity<List<InternshipOrJobDTO>> getInternshipsAndJobsForCurrentUser() {
        return experienceService.getInternshipsAndJobsForCurrentUser();
    }

    @GetMapping("/internships-jobs/{internshipId}")
    public ResponseEntity<InternshipOrJobDTO> getInternshipOrJobById(@PathVariable Long internshipId) {
        return experienceService.getInternshipOrJobById(internshipId);
    }

    // =========================================================
    // 🔹 User Search by Company
    // =========================================================

    @GetMapping("/users-by-company")
    public ResponseEntity<List<UserModel>> getUsersByCompanyName(@RequestParam String companyName) {
        return experienceService.getUsersByCompanyName(companyName);
    }
}
