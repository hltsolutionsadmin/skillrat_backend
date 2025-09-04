package com.skillrat.usermanagement.services;

import org.springframework.http.ResponseEntity;

import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.usermanagement.dto.ExperienceDTO;

public interface SRExperienceService {


	ResponseEntity<MessageResponse> save(ExperienceDTO dto);
	
	
}
