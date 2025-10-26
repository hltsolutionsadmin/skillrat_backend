package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.B2BUnitDTO;
import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.dto.UserDTO;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.utils.Populator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlacementCellPopulator implements Populator<PlacementCellModel, PlacementCellDTO> {

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private B2BUnitPopulator b2BUnitPopulator;

    @Override
    public void populate(PlacementCellModel source, PlacementCellDTO target) {
        if (source == null) {
            return;
        }

        target.setId(source.getId());
        target.setDescription(source.getDescription());

        if (source.getB2bUnit() != null) {
            B2BUnitDTO dto = new B2BUnitDTO();
            b2BUnitPopulator.populate(source.getB2bUnit(), dto);
            target.setB2bUnit(dto);
        }

        if (source.getCoordinator() != null) {
            UserDTO dto = new UserDTO();
            userPopulator.populate(source.getCoordinator(), dto);
            target.setUser(dto);
        }

        target.setCreatedAt(source.getCreatedAt());
    }
}
