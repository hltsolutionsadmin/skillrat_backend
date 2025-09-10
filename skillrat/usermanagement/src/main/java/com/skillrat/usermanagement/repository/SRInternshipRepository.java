package com.skillrat.usermanagement.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.skillrat.usermanagement.model.InternshipModel;
import com.skillrat.usermanagement.model.UserModel;

public interface SRInternshipRepository extends JpaRepository<InternshipModel, Long> {
    List<InternshipModel> findByUser(UserModel user );
    Optional<InternshipModel> findByIdAndUser(Long id, UserModel user);
    Page<InternshipModel> findByUser(UserModel user, Pageable pageable);


}


