package com.skillrat.usermanagement.controllers;

import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.PlacementCellPopulator;
import com.skillrat.usermanagement.services.PlacementCellService;
import com.skillrat.utils.SRBaseEndpoint;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/placement")
public class PlacementCellController extends SRBaseEndpoint {

    @Resource(name = "/placementCellService")
    private PlacementCellService placementCellService;

    @Autowired
    private PlacementCellPopulator populator;

    public ResponseEntity<String> createPlacementCell(PlacementCellDTO dto){
        return null;
    }

    private PlacementCellDTO convert(PlacementCellModel source){
        PlacementCellDTO dto = new PlacementCellDTO();
        populator.populate(source,dto);
        return dto;
    }
}
