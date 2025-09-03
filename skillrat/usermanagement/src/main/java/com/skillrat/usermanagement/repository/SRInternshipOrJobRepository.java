package com.skillrat.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import com.skillrat.usermanagement.model.ExperienceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillrat.usermanagement.model.InternshipOrJobModel;
import com.skillrat.usermanagement.model.UserModel;

@Repository("srInternshipOrJobRepository")
public interface SRInternshipOrJobRepository extends JpaRepository<InternshipOrJobModel, Long> {

//     ✅ Fetch all internships/jobs for a specific user
   List<InternshipOrJobModel> findByUser(UserModel user);



}
