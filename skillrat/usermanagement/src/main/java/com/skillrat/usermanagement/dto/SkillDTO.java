package com.skillrat.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SkillDTO {
    private Long id;
    private String name;

    public SkillDTO() {

    }
}
