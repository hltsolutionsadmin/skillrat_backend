package com.skillrat.usermanagement.controllers;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.services.SRExperienceService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;


import java.util.List;

@RestController
@RequestMapping(value = "/experience")
public class SRExperienceController {

    @Resource(name = "srExperienceService")
    private SRExperienceService experienceService;

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addExperience(@Valid @RequestBody ExperienceDTO dto) {
        return experienceService.save(dto);
    }

//    GetMapping
//    public ResponseEntity<ExperienceProfileDTO> getMyExperience() {
//        return experienceService.getMyExperience();
//    }
@GetMapping
public ResponseEntity<ExperienceDTO> getMyExperience() {
    return experienceService.getExperience();
}


    // ---------------- GET EDUCATION
    @GetMapping("/education")
    public ResponseEntity<List<EducationDTO>> getMyEducation() {
        return experienceService.getEducation();
    }

    @GetMapping("/education/{id}")
    public ResponseEntity<EducationDTO> getEducationById(@PathVariable Long id) {

        return experienceService.getEducationById(id);
    }

    // ---------------- GET INTERNSHIPS
//    @GetMapping("/internships")
//    public ResponseEntity<List<InternshipDTO>> getMyInternships() {
//        return experienceService.getMyInternships();
//    }
    @GetMapping("/internships")
    public ResponseEntity<Page<InternshipDTO>> getMyInternships(Pageable pageable) {
        return experienceService.getInternships(pageable);
    }

    @GetMapping("/internships/{id}")
    public ResponseEntity<InternshipDTO> getInternshipById(@PathVariable Long id) {
        return experienceService.getInternshipById(id);
    }

    // ---------------- GET JOBS
//    @GetMapping("/jobs")
//    public ResponseEntity<List<JobDTO>> getMyJobs() {
//        return experienceService.getMyJobs();
//    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobDTO>> getMyJobs(Pageable pageable) {
        return experienceService.getJobs(pageable);
    }


    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return experienceService.getJobById(id);
    }
}