package com.skillrat.usermanagement.services.impl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
//import com.skillrat.usermanagement.dto.ExperienceProfileDTO;
import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.InternshipModel;
import com.skillrat.usermanagement.model.JobModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.ExperiencePopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SREducationRepository;
import com.skillrat.usermanagement.repository.SRExperienceReposiroty;
import com.skillrat.usermanagement.repository.SRInternshipRepository;
import com.skillrat.usermanagement.repository.SRJobRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.SRBaseEndpoint;
import com.skillrat.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;

import jakarta.annotation.Resource;

@SuppressWarnings("rawtypes")
@Service("srExperienceService")
public class SRExperienceServiceImpl extends SRBaseEndpoint implements SRExperienceService {

    @Resource(name = "srExperienceReposiroty")
    private SRExperienceReposiroty reposiroty;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private B2BUnitRepository b2bUnitRepository;

    @Autowired
    private SREducationRepository educationRepository;

    @Autowired
    private SRInternshipRepository internshipRepository;

    @Autowired
    private SRJobRepository jobRepository;

    @Autowired
    private ExperiencePopulator experiencePopulator;

    // -------------------------- CREATE/UPDATE (already had) --------------------------
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

        reposiroty.save(experience);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    // -------------------------- NEW: READ/GET --------------------------

//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<ExperienceDTO> getExperience() {
//        UserModel currentUser = fetchCurrentUser();
//
//        List<EducationDTO> edu = educationRepository.findByUser(currentUser).stream()
//                .map(this::toEducationDTO)
//                .collect(Collectors.toList());
//
//        List<InternshipDTO> ints = internshipRepository.findByUser(currentUser).stream()
//                .map(this::toInternshipDTO)
//                .collect(Collectors.toList());
//
//        List<JobDTO> jobs = jobRepository.findByUser(currentUser).stream()
//                .map(this::toJobDTO)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(new ExperienceDTO(edu, ints, jobs));
//    }

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
        List<EducationDTO> list = educationRepository.findByUser(currentUser).stream()
                .map(this::toEducationDTO)
                .collect(Collectors.toList());
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

//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<List<InternshipDTO>> getMyInternships() {
//        UserModel currentUser = fetchCurrentUser();
//        List<InternshipDTO> list = internshipRepository.findByUser(currentUser).stream()
//                .map(this::toInternshipDTO)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(list);
//    }

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

//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<List<JobDTO>> getMyJobs() {
//        UserModel currentUser = fetchCurrentUser();
//        List<JobDTO> list = jobRepository.findByUser(currentUser).stream()
//                .map(this::toJobDTO)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(list);
//    }
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

    // -------------------------- MAPPING: Model -> DTO --------------------------
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

    // -------------------------- existing private helpers (unchanged) --------------------------
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

    // -------------------------- COMMON --------------------------
    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }
}
