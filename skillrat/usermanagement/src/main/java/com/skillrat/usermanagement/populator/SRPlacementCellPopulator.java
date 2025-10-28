//package com.skillrat.usermanagement.populator;
//
//import com.skillrat.usermanagement.dto.PlacementCellDTO;
//import com.skillrat.usermanagement.model.PlacementCellModel;
//import com.skillrat.utils.Populator;
//import org.springframework.stereotype.Component;
//
///**
// * Populator for PlacementCellModel <-> PlacementCellDTO
// */
//@Component
//public class PlacementCellPopulator implements Populator<PlacementCellModel, PlacementCellDTO> {
//
//    /**
//     * Converts Model to DTO
//     */
//    public PlacementCellDTO toDTO(PlacementCellModel source) {
//        if (source == null) return null;
//        PlacementCellDTO dto = new PlacementCellDTO();
//        populate(source, dto);
//        return dto;
//    }
//
//    @Override
//    public void populate(PlacementCellModel source, PlacementCellDTO target) {
//        if (source == null) return;
//
//        target.setId(source.getId());
//        //target.setB2bUnit(source.getB2bUnit());
//        target.setPlacementOfficerName(source.getPlacementOfficerName());
//        target.setPlacementOfficerEmail(source.getPlacementOfficerEmail());
//        target.setPlacementOfficerPhone(source.getPlacementOfficerPhone());
//        target.setPlacementOfficeLocation(source.getPlacementOfficeLocation());
//        target.setTotalStudentsRegistered(source.getTotalStudentsRegistered());
//        target.setYearOfEstablishment(source.getYearOfEstablishment());
//        target.setStatus(source.getStatus());
//        target.setCreatedAt(source.getCreatedAt());
//        target.setUpdatedAt(source.getUpdatedAt());
//    }
//}

package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.dto.UserDTO;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.utils.Populator;
import org.springframework.stereotype.Component;

/**
 * Populator for PlacementCellModel <-> PlacementCellDTO
 */
@Component
public class SRPlacementCellPopulator implements Populator<PlacementCellModel, PlacementCellDTO> {

    /**
     * Converts Model to DTO
     */
    public PlacementCellDTO toDTO(PlacementCellModel source) {
        if (source == null) return null;
        PlacementCellDTO dto = new PlacementCellDTO();
        populate(source, dto);
        return dto;
    }

    @Override
    public void populate(PlacementCellModel source, PlacementCellDTO target) {
        if (source == null || target == null) return;

        target.setId(source.getId());

        // Populate B2B Unit reference
        if (source.getB2bUnit() != null) {
            target.setB2bUnitId(source.getB2bUnit().getId());
        }

        // Populate coordinator reference
        if (source.getCoordinator() != null) {
            UserModel coordinator = source.getCoordinator();
            UserDTO coordinatorDTO = new UserDTO();
            coordinatorDTO.setId(coordinator.getId());
            coordinatorDTO.setFullName(coordinator.getFullName());
            coordinatorDTO.setEmail(coordinator.getEmail());
            coordinatorDTO.setPrimaryContact(coordinator.getPrimaryContact());
            target.setCoordinator(coordinatorDTO);
            target.setCoordinatorName(coordinator.getFullName());
        }

        target.setTotalStudentsRegistered(source.getTotalStudentsRegistered());
        target.setYearOfEstablishment(source.getYearOfEstablishment());
        target.setStatus(source.getStatus());
        target.setRemarks(source.getRemarks());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
    }
}
