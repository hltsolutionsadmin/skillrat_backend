package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.usermanagement.dto.ExperienceDTO;


import java.util.List;

public interface SRExperienceService {

	ResponseEntity<MessageResponse> save(ExperienceDTO dto);

    ResponseEntity<ExperienceDTO> getExperience();

    ResponseEntity<List<EducationDTO>> getEducation();
    ResponseEntity<EducationDTO> getEducationById(Long id);


    ResponseEntity<Page<InternshipDTO>> getInternships(Pageable pageable);
    ResponseEntity<InternshipDTO> getInternshipById(Long id);


    ResponseEntity<Page<JobDTO>> getJobs(Pageable pageable);
    ResponseEntity<JobDTO> getJobById(Long id);




}
