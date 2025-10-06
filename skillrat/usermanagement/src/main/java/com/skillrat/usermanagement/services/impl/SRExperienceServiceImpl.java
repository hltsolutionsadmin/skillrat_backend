package com.skillrat.usermanagement.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SREducationRepository;
import com.skillrat.usermanagement.repository.SRExperienceReposiroty;
import com.skillrat.usermanagement.repository.SRInternshipRepository;
import com.skillrat.usermanagement.repository.SRJobRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.SRBaseEndpoint;
import com.skillrat.utils.SecurityUtils;

import static com.skillrat.utils.SRAppConstants.*;

@Service("srExperienceService")
@RequiredArgsConstructor
public class SRExperienceServiceImpl extends SRBaseEndpoint implements SRExperienceService {

    private final SRExperienceReposiroty experienceRepository;
    private final UserRepository userRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final SREducationRepository educationRepository;
    private final SRInternshipRepository internshipRepository;
    private final SRJobRepository jobRepository;

    @Override
    @Transactional
    public ResponseEntity<MessageResponse> save(ExperienceDTO dto) {
        UserModel currentUser = fetchCurrentUser();

        B2BUnitModel b2bUnit = b2bUnitRepository.findById(dto.getB2bUnitId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));

        String type = dto.getType() != null ? dto.getType().toUpperCase() : "";

        // Check if experience already exists
        ExperienceModel experience = experienceRepository
                .findByUserAndB2bUnitAndType(currentUser, b2bUnit, ExperienceType.valueOf(type))
                .orElseGet(() -> {
                    ExperienceModel exp = new ExperienceModel();
                    exp.setUser(currentUser);
                    exp.setB2bUnit(b2bUnit);
                    exp.setType(ExperienceType.valueOf(type));
                    return exp;
                });

        switch (type) {
            case "EDUCATION" -> handleEducation(dto, currentUser, experience);
            case "INTERNSHIP" -> handleInternship(dto, currentUser, experience);
            case "JOB" -> handleJob(dto, currentUser, experience);
            default -> throw new HltCustomerException(ErrorCode.USER_INPUT_INVALID);
        }

        experienceRepository.save(experience);
        return ResponseEntity.ok(new MessageResponse("Experience saved successfully"));
    }




    private void handleEducation(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (!dto.isAddingEducation() || dto.getAcademics() == null || dto.getAcademics().isEmpty()) return;

        List<EducationModel> academics = new ArrayList<>(educationRepository.findByUser(user));
        mergeOrAddEducation(academics, dto.getAcademics(), user);

        for (EducationModel edu : academics) {
            if (!experience.getEducation().contains(edu)) {
                experience.getEducation().add(edu);
            }
        }
    }


    private void handleInternship(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (dto.getInternships() == null || dto.getInternships().isEmpty()) return;

        List<InternshipModel> internships = new ArrayList<>(internshipRepository.findByUser(user));
        mergeOrAddInternships(internships, dto.getInternships(), user);
        experience.setInternships(internships);
    }

    private void handleJob(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (dto.getJobs() == null || dto.getJobs().isEmpty()) return;

        List<JobModel> jobs = new ArrayList<>(jobRepository.findByUser(user));
        mergeOrAddJobs(jobs, dto.getJobs(), user);
        experience.setJobs(jobs);
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ExperienceDTO> getExperience() {
        UserModel currentUser = fetchCurrentUser();

        ExperienceDTO experienceDTO = new ExperienceDTO();
        experienceDTO.setAcademics(educationRepository.findByUser(currentUser).stream().map(this::toEducationDTO).toList());
        experienceDTO.setInternships(internshipRepository.findByUser(currentUser).stream().map(this::toInternshipDTO).toList());
        experienceDTO.setJobs(jobRepository.findByUser(currentUser).stream().map(this::toJobDTO).toList());

        return ResponseEntity.ok(experienceDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EducationDTO>> getEducation() {
        UserModel currentUser = fetchCurrentUser();
        List<EducationDTO> educationDTOList = educationRepository.findByUser(currentUser).stream().map(this::toEducationDTO).toList();
        return ResponseEntity.ok(educationDTOList);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EducationDTO> getEducationById(Long id) {
        UserModel currentUser = fetchCurrentUser();
        return educationRepository.findByIdAndUser(id, currentUser)
                .map(model -> ResponseEntity.ok(toEducationDTO(model)))
                .orElseThrow(() -> new HltCustomerException(ErrorCode.EDUCATION_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<InternshipDTO>> getInternships(Pageable pageable) {
        UserModel currentUser = fetchCurrentUser();
        Page<InternshipDTO> page = internshipRepository.findByUser(currentUser, pageable).map(this::toInternshipDTO);
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
        Page<JobDTO> page = jobRepository.findByUser(currentUser, pageable).map(this::toJobDTO);
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

    private EducationDTO toEducationDTO(EducationModel educationModel) {
        EducationDTO educationDTO = new EducationDTO();
        educationDTO.setLevel(educationModel.getEducationLevel() != null ? educationModel.getEducationLevel().name() : null);
        educationDTO.setInstitution(educationModel.getInstitution());
        educationDTO.setMarks(educationModel.getMarks());
        educationDTO.setCgpa(educationModel.getCgpa());
        educationDTO.setStartDate(educationModel.getStartDate());
        educationDTO.setEndDate(educationModel.getEndDate());
        return educationDTO;
    }

    private InternshipDTO toInternshipDTO(InternshipModel internshipModel) {
        InternshipDTO internshipDTO = new InternshipDTO();
        internshipDTO.setCompanyName(internshipModel.getCompanyName());
        internshipDTO.setRole(internshipModel.getRole());
        internshipDTO.setStartDate(internshipModel.getStartDate());
        internshipDTO.setEndDate(internshipModel.getEndDate());
        internshipDTO.setDescription(internshipModel.getDescription());
        return internshipDTO;
    }

    private JobDTO toJobDTO(JobModel jobModel) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setCompanyName(jobModel.getCompanyName());
        jobDTO.setPosition(jobModel.getPosition());
        jobDTO.setStartDate(jobModel.getStartDate());
        jobDTO.setEndDate(jobModel.getEndDate());
        jobDTO.setDescription(jobModel.getDescription());
        return jobDTO;
    }


    private void mergeOrAddEducation(List<EducationModel> academics, List<EducationDTO> incoming, UserModel user) {
        if (incoming == null || incoming.isEmpty()) return;

        for (EducationDTO dto : incoming) {
            Optional<EducationModel> match = academics.stream()
                    .filter(e -> e.getEducationLevel() != null
                            && dto.getLevel() != null
                            && e.getEducationLevel().name().equalsIgnoreCase(dto.getLevel()))
                    .findFirst();

            if (match.isPresent()) {
                updateEducation(match.get(), dto);
            } else {
                EducationModel newEdu = createEducation(dto, user);
                educationRepository.save(newEdu);
                academics.add(newEdu);
            }
        }
    }


    private void mergeOrAddInternships(List<InternshipModel> internships, List<InternshipDTO> incoming, UserModel user) {
        if (incoming == null) return;
        for (InternshipDTO dto : incoming) {
            Optional<InternshipModel> match = internships.stream()
                    .filter(e -> e.getCompanyName() != null && e.getCompanyName().equalsIgnoreCase(dto.getCompanyName())
                            && e.getRole() != null && e.getRole().equalsIgnoreCase(dto.getRole()))
                    .findFirst();
            if (match.isPresent()) updateInternship(match.get(), dto);
            else internships.add(createInternship(dto, user));
        }
    }

    private void mergeOrAddJobs(List<JobModel> jobs, List<JobDTO> incoming, UserModel user) {
        if (incoming == null) return;
        for (JobDTO dto : incoming) {
            Optional<JobModel> match = jobs.stream()
                    .filter(e -> e.getCompanyName() != null && e.getCompanyName().equalsIgnoreCase(dto.getCompanyName())
                            && e.getPosition() != null && e.getPosition().equalsIgnoreCase(dto.getPosition()))
                    .findFirst();
            if (match.isPresent()) updateJob(match.get(), dto);
            else jobs.add(createJob(dto, user));
        }
    }


    private void updateEducation(EducationModel existing, EducationDTO dto) {
        if (dto.getInstitution() != null) {
            existing.setInstitution(dto.getInstitution());
        }
        if (dto.getCgpa() != null) {
            existing.setCgpa(dto.getCgpa());
        }
        if (dto.getMarks() != null) {
            existing.setMarks(dto.getMarks());
        }
        if (dto.getStartDate() != null) {
            existing.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            existing.setEndDate(dto.getEndDate());
        }
        if (dto.getStudentId() != null) {
            existing.setStudentId(dto.getStudentId());
        }
        if (dto.getLevel() != null) {
            existing.setEducationLevel(EducationLevel.valueOf(dto.getLevel().toUpperCase()));
        }
        existing.setUpdatedAt(LocalDateTime.now());
    }


    private EducationModel createEducation(EducationDTO educationDTO, UserModel user) {
        EducationModel educationModel = new EducationModel();
        educationModel.setEducationLevel(EducationLevel.valueOf(educationDTO.getLevel().toUpperCase()));
        educationModel.setInstitution(educationDTO.getInstitution());
        educationModel.setCgpa(educationDTO.getCgpa());
        educationModel.setMarks(educationDTO.getMarks());
        educationModel.setCreatedAt(LocalDateTime.now());
        educationModel.setUser(user);
        educationModel.setStartDate(educationDTO.getStartDate());
        educationModel.setEndDate(educationDTO.getEndDate());
        educationModel.setStudentId(educationDTO.getStudentId());
        return educationModel;
    }

    private void updateInternship(InternshipModel internshipModel, InternshipDTO internshipDTO) {
        internshipModel.setCompanyName(internshipDTO.getCompanyName());
        internshipModel.setRole(internshipDTO.getRole());
        internshipModel.setStartDate(internshipDTO.getStartDate());
        internshipModel.setEndDate(internshipDTO.getEndDate());
        internshipModel.setDescription(internshipDTO.getDescription());
    }

    private InternshipModel createInternship(InternshipDTO internshipDTO, UserModel user) {
        InternshipModel internshipModel = new InternshipModel();
        internshipModel.setCompanyName(internshipDTO.getCompanyName());
        internshipModel.setRole(internshipDTO.getRole());
        internshipModel.setStartDate(internshipDTO.getStartDate());
        internshipModel.setEndDate(internshipDTO.getEndDate());
        internshipModel.setDescription(internshipDTO.getDescription());
        internshipModel.setUser(user);
        return internshipModel;
    }

    private void updateJob(JobModel jobModel, JobDTO jobDTO) {
        jobModel.setCompanyName(jobDTO.getCompanyName());
        jobModel.setPosition(jobDTO.getPosition());
        jobModel.setStartDate(jobDTO.getStartDate());
        jobModel.setEndDate(jobDTO.getEndDate());
        jobModel.setDescription(jobDTO.getDescription());
    }

    private JobModel createJob(JobDTO jobDTO, UserModel user) {
        JobModel jobModel = new JobModel();
        jobModel.setCompanyName(jobDTO.getCompanyName());
        jobModel.setPosition(jobDTO.getPosition());
        jobModel.setStartDate(jobDTO.getStartDate());
        jobModel.setEndDate(jobDTO.getEndDate());
        jobModel.setDescription(jobDTO.getDescription());
        jobModel.setUser(user);
        return jobModel;
    }


    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }
}
