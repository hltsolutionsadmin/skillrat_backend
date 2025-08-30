package com.skillrat.usermanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.UserModel;

@Repository("srExperienceReposiroty")
public interface SRExperienceReposiroty extends JpaRepository<ExperienceModel, Long> {
	
	List<ExperienceModel> findByUser(UserModel user);
	
}
