package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.model.SkillModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SRSkillRepository extends JpaRepository<SkillModel, Long> {

    Optional<SkillModel> findByName(String name);
    Optional<SkillModel> findByNameIgnoreCase(String name);
}
