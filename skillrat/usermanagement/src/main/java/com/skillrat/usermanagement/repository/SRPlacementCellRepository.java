package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.model.PlacementCellModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SRPlacementCellRepository extends JpaRepository<PlacementCellModel,Long> {

    Optional<PlacementCellModel> findByB2bUnitId(Long b2bUnitId);
}

