package com.skillrat.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import com.skillrat.usermanagement.dto.enums.ExperienceType;
import com.skillrat.usermanagement.model.B2BUnitModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.UserModel;

@Repository("srExperienceReposiroty")
public interface SRExperienceReposiroty extends JpaRepository<ExperienceModel, Long> {
	
	List<ExperienceModel> findByUser(UserModel user);


    Optional<ExperienceModel> findByUserAndB2bUnitAndType(UserModel user, B2BUnitModel b2bUnit, ExperienceType type);
}
