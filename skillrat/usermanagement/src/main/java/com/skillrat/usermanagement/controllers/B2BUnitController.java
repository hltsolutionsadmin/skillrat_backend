package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.ApiResponse;
import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.*;
import com.skillrat.usermanagement.dto.request.B2BUnitRequest;
import com.skillrat.usermanagement.dto.response.B2BUnitListResponse;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.populator.B2BUnitPopulator;
import com.skillrat.usermanagement.populator.UserPopulator;
import com.skillrat.usermanagement.services.B2BUnitService;
import com.skillrat.usermanagement.services.MediaService;
import com.skillrat.utils.JTBaseEndpoint;
import com.skillrat.utils.JuavaryaConstants;
import com.skillrat.utils.SecurityUtils;

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

@RestController
@RequestMapping("/business")
@Slf4j
public class B2BUnitController extends JTBaseEndpoint {

    @Autowired
    private B2BUnitService b2BUnitService;

    @Autowired
    private B2BUnitPopulator b2BUnitPopulator;

    @Autowired
    private  UserPopulator userPopulator;

    @Autowired
    private  MediaService mediaService;

    @PreAuthorize(JuavaryaConstants.ROLE_SUPER_ADMIN)
    @PostMapping("/onboard")
    public ResponseEntity<B2BUnitDTO> createB2BUnit(@Valid @RequestBody B2BUnitRequest request) throws IOException {
        B2BUnitDTO response = b2BUnitService.createOrUpdate(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/list")
    public ResponseEntity<StandardResponse<Page<B2BUnitListResponse>>> listBusinesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<B2BUnitListResponse> businesses = b2BUnitService.listAllPaginated(page, size);
        return ResponseEntity.ok(StandardResponse.page("Businesses fetched successfully", businesses));
    }


    @GetMapping("/{id}")
    public ResponseEntity<B2BUnitDTO> getBusinessById(@PathVariable Long id) {
        return ResponseEntity.ok(b2BUnitService.getById(id));
    }

    @GetMapping("get")
    public ResponseEntity<B2BUnitDTO> getBusinessByToken() {
        log.info("Received request to fetch B2B unit details by token");
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        log.debug("Extracted user details: userId={}", loggedInUser.getId());
        B2BUnitDTO response = b2BUnitService.getById(loggedInUser.getId());
        log.info("Successfully retrieved B2B unit for userId={}", loggedInUser.getId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/status")
    public ResponseEntity<List<B2BUnitStatusDTO>> getBusinessNameAndApprovalStatusForLoggedInUser() {
        List<B2BUnitStatusDTO> b2bUnitStatusList = b2BUnitService.getBusinessNameAndApprovalStatusForLoggedInUser();
        return ResponseEntity.ok(b2bUnitStatusList);
    }



        @GetMapping("/find")
        public ResponseEntity<Page<B2BUnitDTO>> findNearbyUnits(
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

            Page<B2BUnitModel> unitPage = b2BUnitService.findB2BUnitsWithinRadius(latValue, lngValue, radius, postalCode,searchTerm, categoryName, pageable);

            if (unitPage.isEmpty()) {
                Page<B2BUnitDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
                return ResponseEntity.ok(emptyPage);
            }


//            List<B2BUnitDTO> dtoList = unitPage.getContent().stream()
//                    .map(unit -> {
//                        B2BUnitDTO dto = new B2BUnitDTO();
//                        b2BUnitPopulator.populate(unit, dto);
//                        if (unit.getUserModel() != null) {
//                            UserDTO userDTO = new UserDTO();
//                            userPopulator.populate(unit.getUserModel(), userDTO, false);
//                            dto.setUserDTO(userDTO);
//                        }
//                        return dto;
//                    }).toList();

//            Page<B2BUnitDTO> dtoPage = new PageImpl<>(dtoList, pageable, unitPage.getTotalElements());

//            return ResponseEntity.ok(dtoPage);
            return  null;
        }



    @GetMapping("/searchbycity")
    public ResponseEntity<StandardResponse<Page<B2BUnitDTO>>> searchByCity(
            @RequestParam String city,
            @RequestParam String categoryName,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Searching B2B units by city={}, categoryName={}, searchTerm={}", city, categoryName, searchTerm);
        Pageable pageable = PageRequest.of(page, size);
        Page<B2BUnitDTO> result = b2BUnitService.searchByCityAndCategory(city, categoryName, searchTerm, pageable);
        return ResponseEntity.ok(StandardResponse.page("B2B units fetched successfully", result));
    }



    @GetMapping("/business/{id}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("id") Long unitId) {
        AddressDTO addressDTO = b2BUnitService.getAddressByB2BUnitId(unitId);
        return ResponseEntity.ok(addressDTO);
    }

    @PutMapping("/{businessId}/approve")
    public ResponseEntity<StandardResponse<B2BUnitDTO>> approveBusiness(
            @PathVariable Long businessId,
            @RequestParam Long adminUserId
    ) {
        B2BUnitDTO dto = b2BUnitService.approveBusiness(businessId, adminUserId);
        return ResponseEntity.ok(StandardResponse.single("Business approved successfully", dto));
    }



}





