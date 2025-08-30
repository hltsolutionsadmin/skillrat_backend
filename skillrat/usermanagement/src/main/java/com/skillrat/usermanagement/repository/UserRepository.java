package com.skillrat.usermanagement.repository;


import com.skillrat.commonservice.enums.ERole;
import com.skillrat.commonservice.enums.UserVerificationStatus;
import com.skillrat.usermanagement.model.RoleModel;
import com.skillrat.usermanagement.model.UserModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserModel u WHERE u.email = :email AND u.id <> :userId")
    Boolean existsByEmailAndNotUserId(@Param("email") String email, @Param("userId") Long userId);

    Optional<UserModel> findByEmail(String email);

//    Page<UserModel> findByRolesContaining(RoleModel roleModel, Pageable pageable);

    List<UserModel> findByRolesContaining(RoleModel roleModel);

    Boolean existsByPrimaryContact(String primaryContact);

    Optional<UserModel> findByPrimaryContact(String primaryContact);

    @Query("SELECT COUNT(u) FROM UserModel u WHERE u.b2bUnit.id = :businessId")
    long countUsersByBusinessId(@Param("businessId") Long businessId);

    Optional<UserModel> findByPrimaryContactHash(String primaryContact);

    Optional<UserModel> findByEmailHash(String emailHash);
}
