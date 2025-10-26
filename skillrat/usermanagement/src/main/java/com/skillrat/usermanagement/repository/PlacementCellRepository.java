package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.model.PlacementCellModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("placementCellRepository")
public interface PlacementCellRepository extends JpaRepository<PlacementCellModel,Long> {
}
