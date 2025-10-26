package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.model.PlacementCellModel;

public interface PlacementCellService {

    PlacementCellModel createPlacementCell(PlacementCellModel model);

    PlacementCellModel findByBusiness(Long b2bId);
}
