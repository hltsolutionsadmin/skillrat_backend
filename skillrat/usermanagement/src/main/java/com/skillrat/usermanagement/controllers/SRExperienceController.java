package com.skillrat.usermanagement.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.skillrat.commonservice.dto.MessageResponse;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.services.SRExperienceService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/experience")
public class SRExperienceController {

	@Resource(name = "srExperienceService")
	private SRExperienceService experienceService;

	@PostMapping("/add")
	public ResponseEntity<MessageResponse> addExperience(@Valid @RequestBody ExperienceDTO dto) {

		return experienceService.save(dto);
	}
}
