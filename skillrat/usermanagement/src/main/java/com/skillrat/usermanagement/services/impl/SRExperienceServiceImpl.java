package com.skillrat.usermanagement.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.InternshipOrJobDTO;
import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.InternshipOrJobModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.ExperiencePopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SREducationRepository;
import com.skillrat.usermanagement.repository.SRExperienceReposiroty;
import com.skillrat.usermanagement.repository.SRInternshipOrJobRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.SRBaseEndpoint;
import com.skillrat.utils.SecurityUtils;

@Service("srExperienceService")
public class SRExperienceServiceImpl extends SRBaseEndpoint implements SRExperienceService {

    private final SRExperienceReposiroty reposiroty;
    private final UserRepository userRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final SREducationRepository educationRepository;
    private final SRInternshipOrJobRepository internshipOrJobRepository;
    private final ExperiencePopulator experiencePopulator;

    // Single constructor — Spring will autowire all required beans
    public SRExperienceServiceImpl(SRExperienceReposiroty reposiroty,
                                   UserRepository userRepository,
                                   B2BUnitRepository b2bUnitRepository,
                                   SREducationRepository educationRepository,
                                   SRInternshipOrJobRepository internshipOrJobRepository,
                                   ExperiencePopulator experiencePopulator) {
        this.reposiroty = reposiroty;
        this.userRepository = userRepository;
        this.b2bUnitRepository = b2bUnitRepository;
        this.educationRepository = educationRepository;
        this.internshipOrJobRepository = internshipOrJobRepository;
        this.experiencePopulator = experiencePopulator;
    }

    // ---------------------------
    // Save Experience
    // ---------------------------
    @Override
    public ResponseEntity<MessageResponse> save(ExperienceDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Request body is empty"));
        }

        UserModel currentUser = fetchCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid user"));
        }

        ExperienceModel experience = new ExperienceModel();
        experience.setUser(currentUser);

        // Education handling (only when DTO indicates adding academics and type is EDUCATION)
        if (dto.isAddingEducation() && ExperienceType.EDUCATION.toString().equalsIgnoreCase(dto.getType())) {
            List<EducationModel> academics = new ArrayList<>(educationRepository.findByUser(currentUser));
            if (dto.getAcademics() != null) {
                mergeOrAddEducation(academics, dto.getAcademics(), currentUser);
            }
            experience.setEducation(academics);
        }

        // Internship & Job handling (if type is INTERNSHIP or JOB)
        if (dto.getType() != null &&
                (ExperienceType.INTERNSHIP.toString().equalsIgnoreCase(dto.getType())
                        || ExperienceType.JOB.toString().equalsIgnoreCase(dto.getType()))) {

            List<InternshipOrJobModel> existing = new ArrayList<>(internshipOrJobRepository.findByUser(currentUser));
            if (dto.getInternships() != null) {
                mergeOrAddInternship(existing, dto.getInternships(), currentUser, experience);
            }
            experience.setInternshipsAndJobs(existing);
        }

        reposiroty.save(experience);

        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    // ---------------------------
    // Education helpers
    // ---------------------------
    private void mergeOrAddEducation(List<EducationModel> academics, List<EducationDTO> incoming, UserModel user) {
        if (incoming == null || academics == null) return;

        for (EducationDTO educationDTO : incoming) {
            if (educationDTO == null || educationDTO.getLevel() == null) {
                continue;
            }
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
        if (model == null || dto == null) return;
        model.setInstitution(dto.getInstitution());
        model.setMarks(dto.getMarks());
        model.setCgpa(dto.getCgpa());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
    }

    private EducationModel createEducation(EducationDTO dto, UserModel user) {
        EducationModel model = new EducationModel();
        if (dto.getLevel() != null) {
            model.setEducationLevel(EducationLevel.valueOf(dto.getLevel().toUpperCase()));
        }
        model.setInstitution(dto.getInstitution());
        model.setCgpa(dto.getCgpa());
        model.setMarks(dto.getMarks());
        model.setCreatedAt(LocalDateTime.now());
        model.setUser(user);
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        return model;
    }

    // ---------------------------
    // Internship & Job helpers
    // ---------------------------
    private void mergeOrAddInternship(List<InternshipOrJobModel> existing,
                                      List<InternshipOrJobDTO> incoming,
                                      UserModel user,
                                      ExperienceModel experience) {
        if (incoming == null || existing == null) return;

        for (InternshipOrJobDTO dto : incoming) {
            if (dto == null || dto.getCompanyName() == null || dto.getRoleTitle() == null) {
                continue;
            }

            Optional<InternshipOrJobModel> match = existing.stream()
                    .filter(e -> e.getCompanyName() != null && e.getRoleTitle() != null
                            && e.getCompanyName().equalsIgnoreCase(dto.getCompanyName())
                            && e.getRoleTitle().equalsIgnoreCase(dto.getRoleTitle()))
                    .findFirst();

            if (match.isPresent()) {
                updateInternship(match.get(), dto);
            } else {
                existing.add(createInternship(dto, user, experience));
            }
        }
    }

    private void updateInternship(InternshipOrJobModel model, InternshipOrJobDTO dto) {
        if (model == null || dto == null) return;
        model.setCompanyName(dto.getCompanyName());
        model.setRoleTitle(dto.getRoleTitle());
        model.setDescription(dto.getDescription());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
    }

    private InternshipOrJobModel createInternship(InternshipOrJobDTO dto,
                                                  UserModel user,
                                                  ExperienceModel experience) {
        InternshipOrJobModel model = new InternshipOrJobModel();
        model.setUser(user);
        model.setExperience(experience);
        model.setCompanyName(dto.getCompanyName());
        model.setRoleTitle(dto.getRoleTitle());
        model.setDescription(dto.getDescription());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setCreatedAt(LocalDateTime.now());
        return model;
    }

    // ---------------------------
    // Current user helper
    // ---------------------------
    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        if (userDetails == null || userDetails.getId() == null) {
            throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
        }
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    // =========================================================
    // GET Methods
    // =========================================================
    @Override
    public ResponseEntity<List<ExperienceDTO>> getAllExperiencesForCurrentUser() {
        UserModel currentUser = fetchCurrentUser();
        List<ExperienceModel> experiences = reposiroty.findByUser(currentUser);
        List<ExperienceDTO> dtos = experiences.stream()
                .filter(Objects::nonNull)
                .map(experiencePopulator::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<List<EducationDTO>> getEducationsForCurrentUser() {
        UserModel currentUser = fetchCurrentUser();
        List<EducationModel> educations = educationRepository.findByUser(currentUser);
        List<EducationDTO> dtos = educations.stream()
                .filter(Objects::nonNull)
                .map(experiencePopulator::toEducationDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<List<InternshipOrJobDTO>> getInternshipsAndJobsForCurrentUser() {
        UserModel currentUser = fetchCurrentUser();
        List<InternshipOrJobModel> internships = internshipOrJobRepository.findByUser(currentUser);
        List<InternshipOrJobDTO> dtos = internships.stream()
                .filter(Objects::nonNull)
                .map(experiencePopulator::toInternshipOrJobDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<EducationDTO> getEducationById(Long educationId) {
        UserModel currentUser = fetchCurrentUser();
        EducationModel education = educationRepository.findByIdAndUser(educationId, currentUser)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.NOT_FOUND));
        return ResponseEntity.ok(experiencePopulator.toEducationDTO(education));
    }

    @Override
    public ResponseEntity<InternshipOrJobDTO> getInternshipOrJobById(Long internshipId) {
        UserModel currentUser = fetchCurrentUser();
        InternshipOrJobModel internship = internshipOrJobRepository.findByIdAndUser(internshipId, currentUser)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.NOT_FOUND));
        return ResponseEntity.ok(experiencePopulator.toInternshipOrJobDTO(internship));
    }

    @Override
    public ResponseEntity<ExperienceDTO> getExperienceById(Long experienceId) {
        UserModel currentUser = fetchCurrentUser();
        ExperienceModel experience = reposiroty.findByIdAndUser(experienceId, currentUser)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.NOT_FOUND));
        return ResponseEntity.ok(experiencePopulator.toDTO(experience));
    }

    @Override
    public ResponseEntity<List<UserModel>> getUsersByCompanyName(String companyName) {
        List<InternshipOrJobModel> internships = internshipOrJobRepository.findByCompanyNameIgnoreCase(companyName);
        List<UserModel> users = internships.stream()
                .map(InternshipOrJobModel::getUser)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<MessageResponse> updateByUserId(Long userId, ExperienceDTO dto) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        // Update Education
        if (dto.getAcademics() != null && !dto.getAcademics().isEmpty()) {
            List<EducationModel> existing = educationRepository.findByUser(user);
            mergeOrAddEducation(existing, dto.getAcademics(), user);
            educationRepository.saveAll(existing);
        }

        // Update Internship/Job
        if (dto.getInternships() != null && !dto.getInternships().isEmpty()) {
            List<InternshipOrJobModel> existing = internshipOrJobRepository.findByUser(user);
            mergeOrAddInternship(existing, dto.getInternships(), user, null); // no experience linked directly here
            internshipOrJobRepository.saveAll(existing);
        }

        return ResponseEntity.ok(new MessageResponse("Updated education and internship/job for user ID: " + userId));
    }

    @Override
    public ResponseEntity<MessageResponse> deleteAllByUserId(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        // Delete all Education
        List<EducationModel> educations = educationRepository.findByUser(user);
        if (!educations.isEmpty()) {
            educationRepository.deleteAll(educations);
        }

        // Delete all Internship/Job
        List<InternshipOrJobModel> jobs = internshipOrJobRepository.findByUser(user);
        if (!jobs.isEmpty()) {
            internshipOrJobRepository.deleteAll(jobs);
        }

        return ResponseEntity.ok(new MessageResponse("Deleted all education and internship/job for user ID: " + userId));
    }
}
