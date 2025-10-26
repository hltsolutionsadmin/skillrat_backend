package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.B2BUnitDTO;
import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.dto.UserDTO;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.utils.Populator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
public class SRPlacementCellPopulator implements Populator<PlacementCellModel, PlacementCellDTO> {

    private final B2BUnitPopulator b2BUnitPopulator;
    private final UserPopulator userPopulator;

    @Override
    public void populate(PlacementCellModel source, PlacementCellDTO target) {
        if (source == null || target == null) {
            return;
        }

        target.setId(source.getId());
        target.setDescription(source.getDescription());

        if (!ObjectUtils.isEmpty(source.getB2bUnit())) {
            B2BUnitDTO b2bDto = b2BUnitPopulator.toDTO(source.getB2bUnit());
            target.setB2bUnit(b2bDto);
        }

        if (!ObjectUtils.isEmpty(source.getCoordinator())) {
            UserModel coordinator = source.getCoordinator();
            UserDTO userDto = userPopulator.toDTO(coordinator);
            target.setCoordinator(userDto);
            target.setCoordinatorName(coordinator.getFullName());
        }
    }
}
