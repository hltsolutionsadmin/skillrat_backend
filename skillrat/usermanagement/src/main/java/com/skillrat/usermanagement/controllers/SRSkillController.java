package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.SkillDTO;
import com.skillrat.usermanagement.services.SRSkillService;
import com.skillrat.utils.SRAppConstants;
import com.skillrat.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SRSkillController {

    private final SRSkillService skillService;


    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<StandardResponse<SkillDTO>> saveSkill(@RequestBody SkillDTO dto) {
        SkillDTO saved = skillService.saveSkill(dto);
        return ResponseEntity.ok(StandardResponse.single(SRAppConstants.SKILL_SAVED_SUCCESS, saved));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<StandardResponse<SkillDTO>> getSkill(@PathVariable Long id) {
        SkillDTO dto = skillService.getSkillById(id);
        return ResponseEntity.ok(StandardResponse.single(SRAppConstants.SKILL_FETCH_SUCCESS, dto));
    }

    @GetMapping("/admin")
    public ResponseEntity<StandardResponse<Page<SkillDTO>>> getAllSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SkillDTO> skills = skillService.getAllSkills(pageable);
        return ResponseEntity.ok(StandardResponse.page(SRAppConstants.SKILL_FETCH_SUCCESS, skills));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<StandardResponse<String>> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(StandardResponse.message(SRAppConstants.SKILL_DELETED_SUCCESS));
    }

    @PostMapping("/user")
    public ResponseEntity<StandardResponse<List<SkillDTO>>> addOrAssignSkill(
            @RequestParam String name) {

        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        List<SkillDTO> skills = skillService.addOrAssignSkill(userId, name);
        return ResponseEntity.ok(StandardResponse.list(SRAppConstants.SKILL_ADDED_SUCCESS, skills));
    }

    @PostMapping("/user/batch")
    public ResponseEntity<StandardResponse<List<SkillDTO>>> addOrAssignSkills(
            @RequestBody List<String> names) {

        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        List<SkillDTO> skills = skillService.addOrAssignSkills(userId, names);
        return ResponseEntity.ok(StandardResponse.list(SRAppConstants.SKILL_ADDED_SUCCESS, skills));
    }

    @DeleteMapping("/user/{skillId}")
    public ResponseEntity<StandardResponse<List<SkillDTO>>> removeUserSkill(
            @PathVariable Long skillId) {

        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        List<SkillDTO> skills = skillService.removeSkill(userId, skillId);
        return ResponseEntity.ok(StandardResponse.list(SRAppConstants.SKILL_REMOVED_SUCCESS, skills));
    }

    @GetMapping("/user")
    public ResponseEntity<StandardResponse<List<SkillDTO>>> getUserSkills() {

        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        List<SkillDTO> skills = skillService.getUserSkills(userId);
        return ResponseEntity.ok(StandardResponse.list(SRAppConstants.SKILL_FETCH_SUCCESS, skills));
    }

}
