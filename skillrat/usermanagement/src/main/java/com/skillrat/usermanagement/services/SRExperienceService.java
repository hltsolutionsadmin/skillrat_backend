package com.skillrat.usermanagement.services;

import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.InternshipOrJobDTO;
import com.skillrat.usermanagement.model.UserModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SRExperienceService {

    // ✅ Save experience
    ResponseEntity<MessageResponse> save(ExperienceDTO dto);

    // ✅ Get all experiences for current user
    ResponseEntity<List<ExperienceDTO>> getAllExperiencesForCurrentUser();

    // ✅ Get only educations for current user
    ResponseEntity<List<EducationDTO>> getEducationsForCurrentUser();

    // ✅ Get only internships/jobs for current user
    ResponseEntity<List<InternshipOrJobDTO>> getInternshipsAndJobsForCurrentUser();

    // ✅ Get education by ID
     ResponseEntity<EducationDTO> getEducationById(Long educationId);

    // ✅ Get internship/job by ID
    ResponseEntity<InternshipOrJobDTO> getInternshipOrJobById(Long internshipId);

    // ✅ Get experience by ID
    ResponseEntity<ExperienceDTO> getExperienceById(Long experienceId);

    // ✅ Get list of users by company name
    ResponseEntity<List<UserModel>> getUsersByCompanyName(String companyName);

    ResponseEntity<MessageResponse> updateByUserId(Long userId, ExperienceDTO dto);

    ResponseEntity<MessageResponse> deleteAllByUserId(Long userId);

}
