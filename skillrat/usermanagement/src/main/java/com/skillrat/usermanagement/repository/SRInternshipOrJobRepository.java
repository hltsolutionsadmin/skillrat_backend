package com.skillrat.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillrat.usermanagement.model.InternshipOrJobModel;
import com.skillrat.usermanagement.model.UserModel;

@Repository("srInternshipOrJobRepository")
public interface SRInternshipOrJobRepository extends JpaRepository<InternshipOrJobModel, Long> {

   List<InternshipOrJobModel> findByUser(UserModel user);

    Optional<InternshipOrJobModel> findByIdAndUser(Long id, UserModel user);
    List<InternshipOrJobModel> findByCompanyNameIgnoreCase(String companyName);



}
