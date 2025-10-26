package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.PlacementCellRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service("placementCellService")
public class PlacementCellServiceImplImpl implements PlacementCellServiceImpl {

    @Resource(name = "placementCellRepository")
    private PlacementCellRepository cellRepository;

    @Autowired
    private B2BUnitRepository b2BUnitRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PlacementCellModel createPlacementCell(PlacementCellModel model) {
        Optional<UserModel> currentUser = fetchCurrentUser();

        Optional<B2BUnitModel> unit = b2BUnitRepository.findById(model.getB2bUnit().getId());
        if (currentUser.isPresent() && unit.isPresent()
                && unit.get().getType() != null
                && (Arrays.asList("CLG", "UNI").contains(unit.get().getType().name()))
                && currentUser.get().equals(model.getB2bUnit().getAdmin())) {
            return savePlacementDetails(model);
        }
        return null;
    }

    @Override
    public PlacementCellModel findByBusiness(Long b2bId) {
        return null;
    }

    private Optional<UserModel> fetchUser(Long userId) {
        return userRepository.findById(userId);
    }

    private Optional<UserModel> fetchCurrentUser() {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        return Optional.of(userRepository.findById(loggedInUser.getId())).orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    private PlacementCellModel savePlacementDetails(PlacementCellModel placementCell) {
        PlacementCellModel model = new PlacementCellModel();
        model.setDescription(placementCell.getDescription());
        model.setB2bUnit(placementCell.getB2bUnit());

        Optional<UserModel> b2bEmployee = fetchUser(model.getCoordinator().getId());
        if (b2bEmployee.isPresent()) {
            model.setCoordinator(placementCell.getCoordinator());
        }

        model.setCreatedDate(LocalDateTime.now());
        cellRepository.save(model);
        return model;
    }

}
