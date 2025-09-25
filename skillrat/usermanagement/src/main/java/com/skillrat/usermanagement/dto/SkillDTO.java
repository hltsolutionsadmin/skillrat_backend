package com.skillrat.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SkillDTO {
    private Long id;
    private String name;

    public SkillDTO() {

    }

    public SkillDTO(String name) {
    }
}
