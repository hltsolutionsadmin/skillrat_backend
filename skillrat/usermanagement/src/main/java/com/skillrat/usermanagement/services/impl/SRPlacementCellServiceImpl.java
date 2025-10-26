package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SRPlacementCellRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRPlacementCellService;
import com.skillrat.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SRPlacementCellServiceImpl implements SRPlacementCellService {

    private final SRPlacementCellRepository placementCellRepository;
    private final B2BUnitRepository b2BUnitRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PlacementCellModel createPlacementCell(PlacementCellModel model) {
        UserModel currentUser = fetchCurrentUser();

        B2BUnitModel unit = b2BUnitRepository.findById(model.getB2bUnit().getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));

        if (!isEligibleUnitType(unit)) {
            throw new HltCustomerException(ErrorCode.INVALID_BUSINESS_TYPE);
        }

        if (!unit.getAdmin().getId().equals(currentUser.getId())) {
            throw new HltCustomerException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Optional.ofNullable(model.getCoordinator())
                .ifPresent(c -> userRepository.findById(c.getId())
                        .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND)));

        return placementCellRepository.save(model);
    }

    @Override
    public PlacementCellModel findByBusiness(Long b2bId) {
        return placementCellRepository.findByB2bUnitId(b2bId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.PLACEMENT_CELL_NOT_FOUND));
    }

    private UserModel fetchCurrentUser() {
        return userRepository.findById( SecurityUtils.getCurrentUserDetails().getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    private boolean isEligibleUnitType(B2BUnitModel unit) {
        return unit.getType() != null &&
                (unit.getType().name().equals("CLG") || unit.getType().name().equals("UNI"));
    }
}
