package com.hlt.usermanagement.controllers;

import com.hlt.usermanagement.dto.*;
import com.hlt.commonservice.dto.StandardResponse;
import com.hlt.usermanagement.dto.request.B2BUnitRequest;
import com.hlt.usermanagement.dto.response.B2BUnitListResponse;
import com.hlt.usermanagement.model.B2BUnitModel;
import com.hlt.usermanagement.populator.B2BUnitPopulator;
import com.hlt.usermanagement.populator.UserPopulator;
import com.hlt.usermanagement.services.B2BUnitService;
import com.hlt.usermanagement.services.MediaService;
import com.hlt.auth.exception.handling.HltCustomerException;
import com.hlt.auth.exception.handling.ErrorCode;
import com.hlt.utils.JTBaseEndpoint;
import com.hlt.utils.JuavaryaConstants;
import com.hlt.utils.SecurityUtils;
import com.hlt.commonservice.user.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business")
@Slf4j
public class B2BUnitController extends JTBaseEndpoint {

    private static final String MSG_B2B_UNIT_CREATED = "B2B unit created/updated successfully";
    private static final String MSG_B2B_UNIT_FETCHED = "B2B unit fetched successfully";
    private static final String MSG_B2B_UNIT_LIST_FETCHED = "Businesses fetched successfully";
    private static final String MSG_B2B_UNIT_STATUS_FETCHED = "B2B unit statuses fetched successfully";
    private static final String MSG_NEARBY_B2B_UNITS_FETCHED = "Nearby B2B units fetched successfully";
    private static final String MSG_ADDRESS_FETCHED = "Address fetched successfully";
    private static final String MSG_LAT_LNG_REQUIRED = "Latitude and Longitude cannot be null";

    @Autowired
    private B2BUnitService b2BUnitService;
    @Autowired
    private B2BUnitPopulator b2BUnitPopulator;
    @Autowired
    private UserPopulator userPopulator;
    @Autowired
    private MediaService mediaService;


    @PostMapping("/onboard")
    @PreAuthorize(JuavaryaConstants.ROLE_SUPER_ADMIN)
    public ResponseEntity<StandardResponse<B2BUnitDTO>> createB2BUnit(@Valid @RequestBody B2BUnitRequest request) throws IOException {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new HltCustomerException(ErrorCode.BAD_REQUEST, MSG_LAT_LNG_REQUIRED);
        }
        B2BUnitDTO response = b2BUnitService.createOrUpdate(request);
        return ResponseEntity.ok(StandardResponse.single(MSG_B2B_UNIT_CREATED, response));
    }

    @GetMapping("/list")
    public ResponseEntity<StandardResponse<Page<B2BUnitListResponse>>> listBusinesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<B2BUnitListResponse> businesses = b2BUnitService.listAllPaginated(page, size);
        return ResponseEntity.ok(StandardResponse.page(MSG_B2B_UNIT_LIST_FETCHED, businesses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<B2BUnitDTO>> getBusinessById(@PathVariable Long id) {
        B2BUnitDTO dto = b2BUnitService.getById(id);
        return ResponseEntity.ok(StandardResponse.single(MSG_B2B_UNIT_FETCHED, dto));
    }

    @GetMapping("/get")
    public ResponseEntity<StandardResponse<B2BUnitDTO>> getBusinessByToken() {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        B2BUnitDTO dto = b2BUnitService.getById(loggedInUser.getId());
        return ResponseEntity.ok(StandardResponse.single(MSG_B2B_UNIT_FETCHED, dto));
    }

    @GetMapping("/status")
    public ResponseEntity<StandardResponse<List<B2BUnitStatusDTO>>> getBusinessNameAndApprovalStatusForLoggedInUser() {
        List<B2BUnitStatusDTO> b2bUnitStatusList = b2BUnitService.getBusinessNameAndApprovalStatusForLoggedInUser();
        return ResponseEntity.ok(StandardResponse.list(MSG_B2B_UNIT_STATUS_FETCHED, b2bUnitStatusList));
    }

    @GetMapping("/find")
    public ResponseEntity<StandardResponse<Page<B2BUnitDTO>>> findNearbyUnits(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam String categoryName,
            @RequestParam(required = false, defaultValue = "10") double radius,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        double latValue = latitude != null ? latitude : 0;
        double lngValue = longitude != null ? longitude : 0;

        Page<B2BUnitModel> unitPage = b2BUnitService.findB2BUnitsWithinRadius(latValue, lngValue, radius, postalCode, searchTerm, categoryName, pageable);

        List<B2BUnitDTO> dtoList = unitPage.getContent().stream()
                .map(b2BUnitPopulator::toDTO)
                .collect(Collectors.toList());

        Page<B2BUnitDTO> dtoPage = new PageImpl<>(dtoList, pageable, unitPage.getTotalElements());
        return ResponseEntity.ok(StandardResponse.page(MSG_NEARBY_B2B_UNITS_FETCHED, dtoPage));
    }

    @GetMapping("/searchbycity")
    public ResponseEntity<StandardResponse<Page<B2BUnitDTO>>> searchByCity(
            @RequestParam String city,
            @RequestParam String categoryName,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<B2BUnitDTO> result = b2BUnitService.searchByCityAndCategory(city, categoryName, searchTerm, pageable);
        return ResponseEntity.ok(StandardResponse.page(MSG_B2B_UNIT_LIST_FETCHED, result));
    }

    @GetMapping("/business/{id}/address")
    public ResponseEntity<StandardResponse<AddressDTO>> getAddress(@PathVariable("id") Long unitId) {
        AddressDTO addressDTO = b2BUnitService.getAddressByB2BUnitId(unitId);
        return ResponseEntity.ok(StandardResponse.single(MSG_ADDRESS_FETCHED, addressDTO));
    }
}
