package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.SRPlacementCellPopulator;
import com.skillrat.usermanagement.services.SRPlacementCellService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/placementcells")
@RequiredArgsConstructor
public class SRPlacementCellController {

    private final SRPlacementCellService placementCellService;
    private final SRPlacementCellPopulator placementCellPopulator;


    public static final String PLACEMENT_CELL_CREATED = "Placement cell created successfully";
    public static final String PLACEMENT_CELL_FETCHED = "Placement cell fetched successfully";

    @PostMapping
    public StandardResponse<PlacementCellDTO> createPlacementCell(@Valid @RequestBody PlacementCellDTO dto) {

        PlacementCellModel model = new PlacementCellModel();
        model.setDescription(dto.getDescription());
        B2BUnitModel b2bUnit = new B2BUnitModel();
        b2bUnit.setId(dto.getB2bUnit().getId());
        model.setB2bUnit(b2bUnit);

        if (dto.getCoordinator() != null && dto.getCoordinator().getId() != null) {
            UserModel coordinator = new UserModel();
            coordinator.setId(dto.getCoordinator().getId());
            model.setCoordinator(coordinator);
        }

        PlacementCellModel savedModel = placementCellService.createPlacementCell(model);

        PlacementCellDTO responseDto = new PlacementCellDTO();
        placementCellPopulator.populate(savedModel, responseDto);

        return StandardResponse.single(PLACEMENT_CELL_CREATED, responseDto);
    }


    @GetMapping("/{b2bUnitId}")
    public StandardResponse<PlacementCellDTO> getPlacementCellByBusiness(@PathVariable Long b2bUnitId) {
        PlacementCellModel model = placementCellService.findByBusiness(b2bUnitId);

        PlacementCellDTO dto = new PlacementCellDTO();
        placementCellPopulator.populate(model, dto);

        return StandardResponse.single(PLACEMENT_CELL_FETCHED, dto);
    }
}
