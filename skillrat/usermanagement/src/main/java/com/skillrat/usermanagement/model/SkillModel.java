package com.skillrat.usermanagement.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "skills")
@Getter
@Setter
@AllArgsConstructor
public class SkillModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    public SkillModel() {

    }

    public SkillModel(String name) {
    }
}
