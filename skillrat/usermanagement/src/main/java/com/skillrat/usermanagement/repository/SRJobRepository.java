package com.skillrat.usermanagement.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.skillrat.usermanagement.model.JobModel;
import com.skillrat.usermanagement.model.UserModel;

public interface SRJobRepository extends JpaRepository<JobModel, Long> {
    List<JobModel> findByUser(UserModel user );
    Optional<JobModel> findByIdAndUser(Long id, UserModel user);
    Page<JobModel> findByUser(UserModel user, Pageable pageable);

    Optional<JobModel> findByUserAndCompanyNameAndPosition(UserModel user, String companyName, String position);
}
