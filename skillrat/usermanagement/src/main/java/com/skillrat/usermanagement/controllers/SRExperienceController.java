
package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.SRAppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
    @RequestMapping("/v1/experience")
@RequiredArgsConstructor
public class SRExperienceController {

    private final SRExperienceService experienceService;

    /**
     * Add experience (Education / Internship / Job)
     */
    @PostMapping("/add")
    public ResponseEntity<StandardResponse<Void>> addExperience(@Valid @RequestBody ExperienceDTO dto) {
        experienceService.save(dto);
         return ResponseEntity.ok(StandardResponse.message(SRAppConstants.EXPERIENCE_CREATE_SUCCESS));
    }

    /**
     * Get my complete experience
     */
    @GetMapping
    public ResponseEntity<StandardResponse<ExperienceDTO>> getMyExperience() {
        ExperienceDTO dto = experienceService.getExperience().getBody();
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.EXPERIENCE_FETCH_SUCCESS, dto));
    }

    @GetMapping("/education")
    public ResponseEntity<StandardResponse<List<EducationDTO>>> getMyEducation() {
        List<EducationDTO> list = experienceService.getEducation().getBody();
        return ResponseEntity.ok(StandardResponse.list(
                SRAppConstants.EDUCATION_LIST_SUCCESS, list));
    }

    @GetMapping("/education/{id}")
    public ResponseEntity<StandardResponse<EducationDTO>> getEducationById(@PathVariable Long id) {
        EducationDTO dto = experienceService.getEducationById(id).getBody();
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.EDUCATION_FETCH_SUCCESS, dto));
    }

    @GetMapping("/internships")
    public ResponseEntity<StandardResponse<Page<InternshipDTO>>> getMyInternships(Pageable pageable) {
        Page<InternshipDTO> page = experienceService.getInternships(pageable).getBody();
        return ResponseEntity.ok(StandardResponse.page(
                SRAppConstants.INTERNSHIP_LIST_SUCCESS, page));
    }

    @GetMapping("/internships/{id}")
    public ResponseEntity<StandardResponse<InternshipDTO>> getInternshipById(@PathVariable Long id) {
        InternshipDTO dto = experienceService.getInternshipById(id).getBody();
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.INTERNSHIP_FETCH_SUCCESS, dto));
    }


    @GetMapping("/jobs")
    public ResponseEntity<StandardResponse<Page<JobDTO>>> getMyJobs(Pageable pageable) {
        Page<JobDTO> page = experienceService.getJobs(pageable).getBody();
        return ResponseEntity.ok(StandardResponse.page(
                SRAppConstants.JOB_LIST_SUCCESS, page));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<StandardResponse<JobDTO>> getJobById(@PathVariable Long id) {
        JobDTO dto = experienceService.getJobById(id).getBody();
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.JOB_FETCH_SUCCESS, dto));
    }



}
