package com.skillrat.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillrat.usermanagement.model.BusinessCategoryModel;

import java.util.List;
import java.util.Optional;

public interface BusinessCategoryRepository extends JpaRepository<BusinessCategoryModel, Long> {

    Optional<BusinessCategoryModel> findByName(String name);
    boolean existsByName(String name);
    List<BusinessCategoryModel> findByNameContainingIgnoreCase(String name);
    Optional<BusinessCategoryModel> findByNameIgnoreCase(String name);



}
