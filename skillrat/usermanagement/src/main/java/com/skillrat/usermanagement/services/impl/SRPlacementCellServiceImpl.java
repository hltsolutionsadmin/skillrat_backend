
package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.dto.request.PlacementCellRequest;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.PlacementCellModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.SRPlacementCellPopulator;
import com.skillrat.usermanagement.repository.SRPlacementCellRepository;
import com.skillrat.usermanagement.services.SRPlacementCellService;
import com.skillrat.utils.SRBaseEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SRPlacementCellServiceImpl extends SRBaseEndpoint implements SRPlacementCellService {

    private final SRPlacementCellRepository placementCellRepository;
    private final SRPlacementCellPopulator placementCellPopulator;

    @Override
    @Transactional
    public PlacementCellDTO createPlacementCell(PlacementCellRequest request) {
        log.info("Creating Placement Cell for B2B Unit ID: {}", request.getB2bUnitId());

        if (request.getB2bUnitId() != null && placementCellRepository.existsByB2bUnitId(request.getB2bUnitId())) {
            throw new HltCustomerException(ErrorCode.ALREADY_EXISTS, "Placement Cell already exists for this B2B Unit");
        }

        PlacementCellModel model = new PlacementCellModel();
        populateModel(model, request, true);

        PlacementCellModel saved = placementCellRepository.save(model);
        return placementCellPopulator.toDTO(saved);
    }

    @Override
    @Transactional
    public PlacementCellDTO updatePlacementCell(Long placementCellId, PlacementCellRequest request) {
        log.info("Updating Placement Cell with ID: {}", placementCellId);

        PlacementCellModel model = placementCellRepository.findById(placementCellId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.NOT_FOUND));

        populateModel(model, request, false);
        model.setUpdatedAt(LocalDateTime.now());

        PlacementCellModel updated = placementCellRepository.save(model);
        return placementCellPopulator.toDTO(updated);
    }

    @Override
    @Transactional
    public void deletePlacementCell(Long placementCellId) {
        log.info("Deleting Placement Cell with ID: {}", placementCellId);

        PlacementCellModel model = placementCellRepository.findById(placementCellId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.NOT_FOUND));

        placementCellRepository.delete(model);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlacementCellDTO> getPlacementCellById(Long placementCellId) {
        return placementCellRepository.findById(placementCellId)
                .map(placementCellPopulator::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlacementCellDTO> getAllPlacementCells(Pageable pageable) {
        Page<PlacementCellModel> page = placementCellRepository.findAll(pageable);

        List<PlacementCellDTO> dtoList = page.stream()
                .map(placementCellPopulator::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementCellDTO> getPlacementCellsByB2BUnitId(Long b2bUnitId) {
        List<PlacementCellModel> models = placementCellRepository.findByB2bUnitId(b2bUnitId);
        return models.stream().map(placementCellPopulator::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean existsByB2BUnitId(Long b2bUnitId) {
        return placementCellRepository.existsByB2bUnitId(b2bUnitId);
    }

    @Override
    @Transactional
    public PlacementCellDTO onboardPlacementCellIfProvided(Long b2bUnitId, PlacementCellRequest request) {
        log.info("Optional Placement Cell onboarding for B2B Unit ID: {}", b2bUnitId);

        if (request == null) {
            log.info("No placement cell data provided, skipping onboarding.");
            return null;
        }

        if (placementCellRepository.existsByB2bUnitId(b2bUnitId)) {
            log.info("Placement Cell already exists for B2B Unit ID: {}", b2bUnitId);
            return null;
        }

        request.setB2bUnitId(b2bUnitId);
        PlacementCellModel model = new PlacementCellModel();
        populateModel(model, request, true);

        PlacementCellModel saved = placementCellRepository.save(model);
        return placementCellPopulator.toDTO(saved);
    }

    /**
     * Populates PlacementCellModel from PlacementCellRequest
     */
    private void populateModel(PlacementCellModel model, PlacementCellRequest request, boolean isNew) {
        if (request == null || model == null) return;

        Optional.ofNullable(request.getB2bUnitId()).ifPresent(b2bUnitId -> {
            B2BUnitModel unit = new B2BUnitModel();
            unit.setId(b2bUnitId);
            model.setB2bUnit(unit);
        });

        Optional.ofNullable(request.getCoordinatorId()).ifPresent(coordinatorId -> {
            UserModel coordinator = new UserModel();
            coordinator.setId(coordinatorId);
            model.setCoordinator(coordinator);
        });

        Optional.ofNullable(request.getTotalStudentsRegistered()).ifPresent(model::setTotalStudentsRegistered);
        Optional.
                ofNullable(request.getYearOfEstablishment()).ifPresent(model::setYearOfEstablishment);
        Optional.ofNullable(request.getRemarks()).ifPresent(model::setRemarks);
        Optional.ofNullable(request.getStatus()).ifPresent(model::setStatus);

        if (isNew) {
            model.setCreatedAt(LocalDateTime.now());
            model.setStatus(Optional.ofNullable(request.getStatus()).orElse(model.getStatus()));
        }

        model.setUpdatedAt(LocalDateTime.now());
    }
}