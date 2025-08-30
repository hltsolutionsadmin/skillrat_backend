package com.skillrat.usermanagement.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.ExperiencePopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SREducationRepository;
import com.skillrat.usermanagement.repository.SRExperienceReposiroty;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRExperienceService;
import com.skillrat.utils.JTBaseEndpoint;
import com.skillrat.utils.SecurityUtils;

import jakarta.annotation.Resource;

@SuppressWarnings("rawtypes")
@Service("srExperienceService")
public class SRExperienceServiceImpl extends JTBaseEndpoint implements SRExperienceService {

	@Resource(name = "srExperienceReposiroty")
	private SRExperienceReposiroty reposiroty;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private B2BUnitRepository b2bUnitRepository;

	@Autowired
	private SREducationRepository educationRepository;

	@Autowired
	private ExperiencePopulator experiencePopulator;

	@Override
	public ResponseEntity<MessageResponse> save(ExperienceDTO dto) {
		UserModel currentUser = fetchCurrentUser();

		if (currentUser == null || currentUser.getId() == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("Invalid user"));
		}

		ExperienceModel experience = new ExperienceModel();
		experience.setUser(currentUser);

		if (dto.isAddingEducation() && ExperienceType.EDUCATION.toString().equals(dto.getType())) {
			List<EducationModel> academics = new ArrayList<>(educationRepository.findByUser(currentUser));
			mergeOrAddEducation(academics, dto.getAcademics(), currentUser);
			experience.setEducation(academics);
		}

		reposiroty.save(experience);

		return ResponseEntity.ok(new MessageResponse("Success"));
	}

	private void mergeOrAddEducation(List<EducationModel> academics, List<EducationDTO> incoming, UserModel user) {
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

	private UserModel fetchCurrentUser() {
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		return userRepository.findById(userDetails.getId())
				.orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
	}
}
