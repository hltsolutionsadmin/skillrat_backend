package com.skillrat.usermanagement.services.impl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import com.skillrat.usermanagement.dto.*;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.populator.ExperiencePopulator;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.SRBaseEndpoint;
import com.skillrat.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;


@SuppressWarnings("rawtypes")
@Service("srExperienceService")
@RequiredArgsConstructor
public class SRExperienceServiceImpl extends SRBaseEndpoint implements SRExperienceService {

    private final SRExperienceReposiroty reposiroty;
    private final UserRepository userRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final SREducationRepository educationRepository;
    private final SRInternshipRepository internshipRepository;
    private final SRJobRepository jobRepository;
    private final ExperiencePopulator experiencePopulator;
    private final SRSkillRepository skillRepository;

    @Override
    public ResponseEntity<MessageResponse> save(ExperienceDTO dto) {
        UserModel currentUser = fetchCurrentUser();

        if (currentUser == null || currentUser.getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid user"));
        }

        ExperienceModel experience = new ExperienceModel();
        experience.setUser(currentUser);

        // EDUCATION
        if (dto.isAddingEducation() && ExperienceType.EDUCATION.toString().equals(dto.getType())) {
            List<EducationModel> academics = new ArrayList<>(educationRepository.findByUser(currentUser));
            mergeOrAddEducation(academics, dto.getAcademics(), currentUser);
            experience.setEducation(academics);
        }

        // INTERNSHIP
        if (dto.getInternships() != null && !dto.getInternships().isEmpty()
                && ExperienceType.INTERNSHIP.toString().equals(dto.getType())) {
            List<InternshipModel> internships = new ArrayList<>(internshipRepository.findByUser(currentUser));
            mergeOrAddInternships(internships, dto.getInternships(), currentUser);
            experience.setInternships(internships);
        }

        // JOB
        if (dto.getJobs() != null && !dto.getJobs().isEmpty()
                && ExperienceType.JOB.toString().equals(dto.getType())) {
            List<JobModel> jobs = new ArrayList<>(jobRepository.findByUser(currentUser));
            mergeOrAddJobs(jobs, dto.getJobs(), currentUser);
            experience.setJobs(jobs);
        }
        if (dto.getSkills() != null && !dto.getSkills().isEmpty()) {
            List<SkillModel> skills = new ArrayList<>();

            // Load existing skills for the user
            Set<SkillModel> existingSkills = currentUser.getSkills();

            for (SkillDTO skillDTO : dto.getSkills()) {
                // Check if the skill already exists globally
                SkillModel skill = skillRepository.findByName(skillDTO.getName())
                        .orElseGet(() -> skillRepository.save(new SkillModel(skillDTO.getName())));

                // Add to experience
                skills.add(skill);

                // Add to user's aggregated skill set if not already present
                existingSkills.add(skill);
            }

            // Set skills on experience
            experience.setSkills(skills);

            // Persist user's updated aggregated skills
            currentUser.setSkills(existingSkills);
            userRepository.save(currentUser);
        }

        reposiroty.save(experience);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }


    @Override
    public ResponseEntity<ExperienceDTO> getExperience() {
        UserModel currentUser = fetchCurrentUser();

        List<EducationDTO> eduList = educationRepository.findByUser(currentUser)
                .stream().map(this::toEducationDTO).toList();

        List<InternshipDTO> internshipList = internshipRepository.findByUser(currentUser)
                .stream().map(this::toInternshipDTO).toList();

        List<JobDTO> jobList = jobRepository.findByUser(currentUser)
                .stream().map(this::toJobDTO).toList();

        ExperienceDTO dto = ExperienceDTO.builder()
                .academics(eduList)
                .internships(internshipList)
                .jobs(jobList)
                .build();

        return ResponseEntity.ok(dto);
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EducationDTO>> getEducation() {
        UserModel currentUser = fetchCurrentUser();
        List<EducationDTO> list = educationRepository.findByUser(currentUser).stream().map(this::toEducationDTO).toList();

        return ResponseEntity.ok(list);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EducationDTO> getEducationById(Long id) {
        UserModel currentUser = fetchCurrentUser();
        return educationRepository.findByIdAndUser(id, currentUser)
                .map(model -> ResponseEntity.ok(toEducationDTO(model)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<InternshipDTO>> getInternships(Pageable pageable) {
        UserModel currentUser = fetchCurrentUser();
        Page<InternshipDTO> page = internshipRepository.findByUser(currentUser, pageable)
                .map(this::toInternshipDTO);
        return ResponseEntity.ok(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<InternshipDTO> getInternshipById(Long id) {
        UserModel currentUser = fetchCurrentUser();
        return internshipRepository.findByIdAndUser(id, currentUser)
                .map(model -> ResponseEntity.ok(toInternshipDTO(model)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<JobDTO>> getJobs(Pageable pageable) {
        UserModel currentUser = fetchCurrentUser();
        Page<JobDTO> page = jobRepository.findByUser(currentUser, pageable)
                .map(this::toJobDTO);
        return ResponseEntity.ok(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<JobDTO> getJobById(Long id) {
        UserModel currentUser = fetchCurrentUser();
        return jobRepository.findByIdAndUser(id, currentUser)
                .map(model -> ResponseEntity.ok(toJobDTO(model)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    private EducationDTO toEducationDTO(EducationModel m) {
        EducationDTO dto = new EducationDTO();
        dto.setLevel(m.getEducationLevel() != null ? m.getEducationLevel().name() : null);
        dto.setInstitution(m.getInstitution());
        dto.setMarks(m.getMarks());
        dto.setCgpa(m.getCgpa());
        dto.setStartDate(m.getStartDate());
        dto.setEndDate(m.getEndDate());
        return dto;
    }

    private InternshipDTO toInternshipDTO(InternshipModel m) {
        InternshipDTO dto = new InternshipDTO();
        dto.setCompanyName(m.getCompanyName());
        dto.setRole(m.getRole());
        dto.setStartDate(m.getStartDate());
        dto.setEndDate(m.getEndDate());
        dto.setDescription(m.getDescription());
        return dto;
    }

    private JobDTO toJobDTO(JobModel m) {
        JobDTO dto = new JobDTO();
        dto.setCompanyName(m.getCompanyName());
        dto.setPosition(m.getPosition());
        dto.setStartDate(m.getStartDate());
        dto.setEndDate(m.getEndDate());
        dto.setDescription(m.getDescription());
        return dto;
    }

    private void mergeOrAddEducation(List<EducationModel> academics, List<EducationDTO> incoming, UserModel user) {
        if (incoming == null || incoming.isEmpty()) {
            return;
        }
        for (EducationDTO educationDTO : incoming) {
            Optional<EducationModel> match = academics.stream()
                    .filter(existing -> existing.getEducationLevel() != null
                            && existing.getEducationLevel().name().equalsIgnoreCase(educationDTO.getLevel()))
                    .findFirst();

            if (match.isPresent()) {
                updateEducation(match.get(), educationDTO);
            } else {
                academics.add(createEducation(educationDTO, user));
            }
        }
    }

    private void updateEducation(EducationModel model, EducationDTO dto) {
        model.setInstitution(dto.getInstitution());
        model.setMarks(dto.getMarks());
        model.setCgpa(dto.getCgpa());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
    }

    private EducationModel createEducation(EducationDTO dto, UserModel user) {
        EducationModel model = new EducationModel();
        model.setEducationLevel(EducationLevel.valueOf(dto.getLevel().toUpperCase()));
        model.setInstitution(dto.getInstitution());
        model.setCgpa(dto.getCgpa());
        model.setMarks(dto.getMarks());
        model.setCreatedAt(LocalDateTime.now());
        model.setUser(user);
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        return model;
    }

    private void mergeOrAddInternships(List<InternshipModel> internships, List<InternshipDTO> incoming, UserModel user) {
        if (incoming == null || incoming.isEmpty()) {
            return;
        }
        for (InternshipDTO internshipDTO : incoming) {
            Optional<InternshipModel> match = internships.stream()
                    .filter(existing -> existing.getCompanyName() != null && internshipDTO.getCompanyName() != null
                            && existing.getCompanyName().equalsIgnoreCase(internshipDTO.getCompanyName())
                            && existing.getRole() != null && internshipDTO.getRole() != null
                            && existing.getRole().equalsIgnoreCase(internshipDTO.getRole()))
                    .findFirst();

            if (match.isPresent()) {
                updateInternship(match.get(), internshipDTO);
            } else {
                internships.add(createInternship(internshipDTO, user));
            }
        }
    }

    private void updateInternship(InternshipModel model, InternshipDTO dto) {
        model.setCompanyName(dto.getCompanyName());
        model.setRole(dto.getRole());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setDescription(dto.getDescription());
    }

    private InternshipModel createInternship(InternshipDTO dto, UserModel user) {
        InternshipModel model = new InternshipModel();
        model.setCompanyName(dto.getCompanyName());
        model.setRole(dto.getRole());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setDescription(dto.getDescription());
        model.setUser(user);
        return model;
    }

    private void mergeOrAddJobs(List<JobModel> jobs, List<JobDTO> incoming, UserModel user) {
        if (incoming == null || incoming.isEmpty()) {
            return;
        }
        for (JobDTO jobDTO : incoming) {
            Optional<JobModel> match = jobs.stream()
                    .filter(existing -> existing.getCompanyName() != null && jobDTO.getCompanyName() != null
                            && existing.getCompanyName().equalsIgnoreCase(jobDTO.getCompanyName())
                            && existing.getPosition() != null && jobDTO.getPosition() != null
                            && existing.getPosition().equalsIgnoreCase(jobDTO.getPosition()))
                    .findFirst();

            if (match.isPresent()) {
                updateJob(match.get(), jobDTO);
            } else {
                jobs.add(createJob(jobDTO, user));
            }
        }
    }

    private void updateJob(JobModel model, JobDTO dto) {
        model.setCompanyName(dto.getCompanyName());
        model.setPosition(dto.getPosition());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setDescription(dto.getDescription());
    }

    private JobModel createJob(JobDTO dto, UserModel user) {
        JobModel model = new JobModel();
        model.setCompanyName(dto.getCompanyName());
        model.setPosition(dto.getPosition());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setDescription(dto.getDescription());
        model.setUser(user);
        return model;
    }

    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }
}
