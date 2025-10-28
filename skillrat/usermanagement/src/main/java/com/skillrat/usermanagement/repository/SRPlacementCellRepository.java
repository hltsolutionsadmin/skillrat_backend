
package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.model.PlacementCellModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SRPlacementCellRepository extends JpaRepository<PlacementCellModel, Long> {

    // Find placement cells by B2B Unit ID
    List<PlacementCellModel> findByB2bUnitId(Long b2bUnitId);

    // Check if a placement cell already exists for a given B2B Unit
    boolean existsByB2bUnitId(Long b2bUnitId);

    // Find placement cell by coordinator ID (new version)
    Optional<PlacementCellModel> findByCoordinatorId(Long coordinatorId);
}