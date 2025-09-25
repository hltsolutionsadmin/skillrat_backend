package com.skillrat.usermanagement.services.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.*;
import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.populator.ExperiencePopulator;
import com.skillrat.usermanagement.repository.*;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.SRBaseEndpoint;
import com.skillrat.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("srExperienceService")
@RequiredArgsConstructor
public class SRExperienceServiceImpl extends SRBaseEndpoint implements SRExperienceService {

    private final SRExperienceReposiroty experienceRepository;
    private final UserRepository userRepository;
    private final SREducationRepository educationRepository;
    private final SRInternshipRepository internshipRepository;
    private final SRJobRepository jobRepository;
    private final SRSkillRepository skillRepository;
    private final ExperiencePopulator experiencePopulator;

    @Override
    @Transactional
    public ResponseEntity<MessageResponse> save(ExperienceDTO dto) {
        UserModel user = fetchCurrentUser();
        validateUser(user);

        ExperienceModel experience = new ExperienceModel();
        experience.setUser(user);

        handleEducation(dto, user, experience);
        handleInternships(dto, user, experience);
        handleJobs(dto, user, experience);
        handleSkills(dto, user, experience);

        experienceRepository.save(experience);
        return ResponseEntity.ok(new MessageResponse("Experience saved successfully"));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ExperienceDTO> getExperience() {
        UserModel user = fetchCurrentUser();
        ExperienceDTO dto = ExperienceDTO.builder().academics(educationRepository.findByUser(user).stream().map(this::toEducationDTO).toList()).internships(internshipRepository.findByUser(user).stream().map(this::toInternshipDTO).toList()).jobs(jobRepository.findByUser(user).stream().map(this::toJobDTO).toList()).skills(user.getSkills().stream().map(this::toSkillDTO).toList()).build();
        return ResponseEntity.ok(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EducationDTO>> getEducation() {
        UserModel user = fetchCurrentUser();
        return ResponseEntity.ok(educationRepository.findByUser(user).stream().map(this::toEducationDTO).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EducationDTO> getEducationById(Long id) {
        UserModel user = fetchCurrentUser();
        return educationRepository.findByIdAndUser(id, user).map(this::toEducationDTO).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<InternshipDTO>> getInternships(Pageable pageable) {
        UserModel user = fetchCurrentUser();
        return ResponseEntity.ok(internshipRepository.findByUser(user, pageable).map(this::toInternshipDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<InternshipDTO> getInternshipById(Long id) {
        UserModel user = fetchCurrentUser();
        return internshipRepository.findByIdAndUser(id, user).map(this::toInternshipDTO).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<JobDTO>> getJobs(Pageable pageable) {
        UserModel user = fetchCurrentUser();
        return ResponseEntity.ok(jobRepository.findByUser(user, pageable).map(this::toJobDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<JobDTO> getJobById(Long id) {
        UserModel user = fetchCurrentUser();
        return jobRepository.findByIdAndUser(id, user).map(this::toJobDTO).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private SkillDTO toSkillDTO(SkillModel model) {
        return new SkillDTO(model.getName());
    }

    private EducationDTO toEducationDTO(EducationModel model) {
        EducationDTO dto = new EducationDTO();
        dto.setLevel(model.getEducationLevel() != null ? model.getEducationLevel().name() : null);
        dto.setInstitution(model.getInstitution());
        dto.setMarks(model.getMarks());
        dto.setCgpa(model.getCgpa());
        dto.setStartDate(model.getStartDate());
        dto.setEndDate(model.getEndDate());
        return dto;
    }

    private InternshipDTO toInternshipDTO(InternshipModel model) {
        InternshipDTO dto = new InternshipDTO();
        dto.setCompanyName(model.getCompanyName());
        dto.setRole(model.getRole());
        dto.setStartDate(model.getStartDate());
        dto.setEndDate(model.getEndDate());
        dto.setDescription(model.getDescription());
        return dto;
    }


    private JobDTO toJobDTO(JobModel model) {
        JobDTO dto = new JobDTO();
        dto.setCompanyName(model.getCompanyName());
        dto.setPosition(model.getPosition());
        dto.setStartDate(model.getStartDate());
        dto.setEndDate(model.getEndDate());
        dto.setDescription(model.getDescription());
        return dto;
    }


    private void handleEducation(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (!isEducation(dto)) return;
        Set<EducationModel> existing = new HashSet<>(educationRepository.findByUser(user));
        mergeOrAdd(existing, dto.getAcademics(), e -> e.getEducationLevel() != null ? e.getEducationLevel().name() : null, EducationDTO::getLevel, this::updateEducation, d -> createEducation(d, user));
        experience.setEducation(existing);
    }

    private void handleInternships(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (!isInternship(dto)) return;
        Set<InternshipModel> existing = new HashSet<>(internshipRepository.findByUser(user));
        mergeOrAdd(existing, dto.getInternships(), i -> i.getCompanyName() + "|" + i.getRole(), d -> d.getCompanyName() + "|" + d.getRole(), this::updateInternship, d -> createInternship(d, user));
        experience.setInternships(existing);
    }

    private void handleJobs(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (!isJob(dto)) return;

        Set<JobModel> existingJobs = new HashSet<>(experience.getJobs() != null ? experience.getJobs() : new HashSet<>());

        for (JobDTO jobDTO : dto.getJobs()) {
            Optional<JobModel> optionalJob = jobRepository.findByUserAndCompanyNameAndPosition(user, jobDTO.getCompanyName(), jobDTO.getPosition());

            JobModel job;
            if (optionalJob.isPresent()) {
                job = optionalJob.get();
                updateJob(job, jobDTO);
            } else {
                job = createJob(jobDTO, user);
            }

            existingJobs.add(job);
        }

        experience.setJobs(existingJobs);
    }


    private void handleSkills(ExperienceDTO dto, UserModel user, ExperienceModel experience) {
        if (dto.getSkills() == null || dto.getSkills().isEmpty()) return;

        Set<SkillModel> userSkills = user.getSkills() != null ? new HashSet<>(user.getSkills()) : new HashSet<>();
        Set<SkillModel> experienceSkills = new HashSet<>();

        for (SkillDTO skillDTO : dto.getSkills()) {
            SkillModel skill = skillRepository.findByName(skillDTO.getName()).orElseGet(() -> skillRepository.save(new SkillModel(skillDTO.getName())));
            userSkills.add(skill);
            experienceSkills.add(skill);
        }

        user.setSkills(userSkills);
        experience.setSkills(experienceSkills);
        userRepository.save(user);
    }

    private <M, D> void mergeOrAdd(Set<M> existing, List<D> incoming, Function<M, String> existingKeyExtractor, Function<D, String> incomingKeyExtractor, BiConsumer<M, D> updater, Function<D, M> creator) {
        if (incoming == null || incoming.isEmpty()) return;

        for (D dto : incoming) {
            String key = incomingKeyExtractor.apply(dto);
            existing.stream().filter(e -> Objects.equals(existingKeyExtractor.apply(e), key)).findFirst().ifPresentOrElse(m -> updater.accept(m, dto), () -> existing.add(creator.apply(dto)));
        }
    }

    private void updateEducation(EducationModel model, EducationDTO dto) {
        model.setInstitution(dto.getInstitution());
        model.setMarks(dto.getMarks());
        model.setCgpa(dto.getCgpa());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
    }

    private void updateInternship(InternshipModel model, InternshipDTO dto) {
        model.setCompanyName(dto.getCompanyName());
        model.setRole(dto.getRole());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setDescription(dto.getDescription());
    }

    private void updateJob(JobModel model, JobDTO dto) {
        model.setCompanyName(dto.getCompanyName());
        model.setPosition(dto.getPosition());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setDescription(dto.getDescription());
    }

    private EducationModel createEducation(EducationDTO dto, UserModel user) {
        EducationModel model = new EducationModel();
        model.setEducationLevel(EducationLevel.valueOf(dto.getLevel().toUpperCase()));
        model.setInstitution(dto.getInstitution());
        model.setCgpa(dto.getCgpa());
        model.setMarks(dto.getMarks());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setCreatedAt(LocalDateTime.now());
        model.setUser(user);
        return model;
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

    private void validateUser(UserModel user) {
        if (user == null || user.getId() == null) {
            throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private boolean isEducation(ExperienceDTO dto) {
        return dto.isAddingEducation() && ExperienceType.EDUCATION.name().equals(dto.getType());
    }

    private boolean isInternship(ExperienceDTO dto) {
        return dto.getInternships() != null && !dto.getInternships().isEmpty() && ExperienceType.INTERNSHIP.name().equals(dto.getType());
    }

    private boolean isJob(ExperienceDTO dto) {
        return dto.getJobs() != null && !dto.getJobs().isEmpty() && ExperienceType.JOB.name().equals(dto.getType());
    }

    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        return userRepository.findById(userDetails.getId()).orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }
}
