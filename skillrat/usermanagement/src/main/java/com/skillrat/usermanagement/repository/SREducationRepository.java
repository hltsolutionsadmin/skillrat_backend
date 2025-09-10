package com.skillrat.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillrat.usermanagement.dto.enums.EducationLevel;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.UserModel;

public interface SREducationRepository extends JpaRepository<EducationModel, Long> {

	List<EducationModel> findByUser(UserModel user);

	EducationModel findByUserAndEducationLevel(UserModel user, EducationLevel level);

    Optional<EducationModel> findByIdAndUser(Long id, UserModel user);


}
