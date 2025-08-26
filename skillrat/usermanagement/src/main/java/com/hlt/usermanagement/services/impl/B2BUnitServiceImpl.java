package com.hlt.usermanagement.services.impl;

import com.hlt.usermanagement.azure.service.AwsBlobService;
import com.hlt.usermanagement.dto.*;
import com.hlt.usermanagement.dto.request.B2BUnitRequest;
import com.hlt.usermanagement.dto.request.ProductAttributeRequest;
import com.hlt.usermanagement.dto.response.B2BUnitListResponse;
import com.hlt.usermanagement.dto.response.BusinessAttributeResponse;
import com.hlt.usermanagement.model.*;
import com.hlt.usermanagement.populator.*;
import com.hlt.usermanagement.repository.*;
import com.hlt.usermanagement.services.B2BUnitService;
import com.hlt.auth.exception.handling.HltCustomerException;
import com.hlt.auth.exception.handling.ErrorCode;
import com.hlt.commonservice.dto.Role;
import com.hlt.commonservice.user.UserDetailsImpl;
import com.hlt.utils.JTBaseEndpoint;
import com.hlt.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class B2BUnitServiceImpl extends JTBaseEndpoint implements B2BUnitService {

    @Autowired private BusinessCategoryRepository categoryRepository;
    @Autowired private B2BUnitRepository b2bUnitRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private B2BUnitPopulator b2bUnitPopulator;
    @Autowired private UserPopulator userPopulator;
    @Autowired private AddressPopulator addressPopulator;
    @Autowired private BusinessAttributePopulator attributePopulator;
    @Autowired private AwsBlobService awsBlobService;

    @Override
    @Transactional
    public B2BUnitDTO createOrUpdate(B2BUnitRequest request) throws IOException {
        UserModel currentUser = fetchCurrentUser();
        Optional<B2BUnitModel> existingOpt = b2bUnitRepository
                .findByAdminAndBusinessNameIgnoreCase(currentUser, request.getBusinessName());

        B2BUnitModel unit = existingOpt.orElseGet(B2BUnitModel::new);
        unit.setAdmin(currentUser);

        // Check if the businessCode already exists
        if (request.getBusinessCode() != null) {
            boolean codeExists = b2bUnitRepository.existsByBusinessCode(request.getBusinessCode());
            if (codeExists && (unit.getId() == null || !unit.getBusinessCode().equals(request.getBusinessCode()))) {
                throw new HltCustomerException(ErrorCode.BUSINESS_CODE_ALREADY_EXISTS);
            }
            unit.setBusinessCode(request.getBusinessCode());
        } else if (unit.getId() == null) {
            // Generate new businessCode only for new units
            unit.setBusinessCode(generateBusinessCode());
        }

        populateBasicDetails(unit, request);
        populateAddress(unit, request);
        populateCategory(unit, request);
        populateAttributes(unit, request);

        B2BUnitModel saved = b2bUnitRepository.save(unit);
        return buildResponseDTO(saved);
    }

    private String generateBusinessCode() {
        return "BUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    private void populateBasicDetails(B2BUnitModel unit, B2BUnitRequest request) {
        if (request.getBusinessName() != null) unit.setBusinessName(request.getBusinessName());
        if (request.getContactNumber() != null) unit.setContactNumber(request.getContactNumber());
        if (request.getLatitude() != null) unit.setBusinessLatitude(request.getLatitude());
        if (request.getLongitude() != null) unit.setBusinessLongitude(request.getLongitude());
        if (request.getBusinessType() != null) unit.setType(request.getBusinessType());
        if (request.getEnabled() != null) unit.setEnabled(request.getEnabled());
        if (request.getTemporarilyClosed() != null) unit.setIsTemporarilyClosed(request.getTemporarilyClosed());
    }

    private void populateAddress(B2BUnitModel unit, B2BUnitRequest request) {
        AddressModel address = Optional.ofNullable(unit.getBusinessAddress()).orElse(new AddressModel());

        if (request.getAddressLine1() != null) address.setAddressLine1(request.getAddressLine1());
        if (request.getStreet() != null) address.setStreet(request.getStreet());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getLatitude() != null) address.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) address.setLongitude(request.getLongitude());

        unit.setBusinessAddress(address);
    }

    private void populateCategory(B2BUnitModel unit, B2BUnitRequest request) {
        if (request.getCategoryId() != null) {
            BusinessCategoryModel category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.CATEGORY_NOT_FOUND));
            unit.setCategory(category);
        }
    }

    private void populateAttributes(B2BUnitModel unit, B2BUnitRequest request) {
        if (request.getAttributes() == null || request.getAttributes().isEmpty()) return;

        Set<BusinessAttributeModel> attributes = Optional.ofNullable(unit.getAttributes())
                .orElseGet(() -> {
                    Set<BusinessAttributeModel> newSet = new HashSet<>();
                    unit.setAttributes(newSet);
                    return newSet;
                });

        attributes.clear();
        for (ProductAttributeRequest attr : request.getAttributes()) {
            BusinessAttributeModel model = new BusinessAttributeModel();
            model.setAttributeName(attr.getAttributeName());
            model.setAttributeValue(attr.getAttributeValue());
            model.setB2bUnitModel(unit);
            attributes.add(model);
        }
    }

    private B2BUnitDTO buildResponseDTO(B2BUnitModel savedModel) {
        B2BUnitDTO dto = b2bUnitPopulator.toDTO(savedModel);
        if (savedModel.getAdmin() != null) {
            UserDTO userDTO = new UserDTO();
            userPopulator.populate(savedModel.getAdmin(), userDTO);
            dto.setAdmin(userDTO);
        }
        return dto;
    }

    @Override
    public Page<B2BUnitListResponse> listAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<B2BUnitModel> b2bUnits = b2bUnitRepository.findAll(pageable);
        return b2bUnits.map(this::mapToB2BUnitListResponse);
    }

    private B2BUnitListResponse mapToB2BUnitListResponse(B2BUnitModel model) {
        B2BUnitListResponse response = new B2BUnitListResponse();
        response.setId(model.getId());
        response.setBusinessName(model.getBusinessName());
        response.setEnabled(model.isEnabled());
        response.setCreationDate(model.getCreationDate());

        if (model.getCategory() != null) response.setCategoryName(model.getCategory().getName());
        if (model.getAdmin() != null) response.setUserId(model.getAdmin().getId());

        if (model.getAttributes() != null && !model.getAttributes().isEmpty()) {
            Set<BusinessAttributeResponse> attributes = model.getAttributes().stream()
                    .map(attr -> new BusinessAttributeResponse(
                            attr.getId(),
                            attr.getAttributeName(),
                            attr.getAttributeValue()))
                    .collect(Collectors.toSet());
            response.setAttributes(attributes);
        }
        return response;
    }

    @Override
    public B2BUnitDTO getById(Long id) {
        B2BUnitModel model = b2bUnitRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));
        return buildResponseDTO(model);
    }

    @Override
    public List<B2BUnitStatusDTO> getBusinessNameAndApprovalStatusForLoggedInUser() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        Set<Role> userRoles = user.getRoles().stream()
                .map(role -> new Role(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        List<B2BUnitModel> units = b2bUnitRepository.findByAdminId(userId);
        if (units.isEmpty()) return List.of(B2BUnitStatusDTO.rolesOnly(userRoles));

        return units.stream()
                .map(unit -> new B2BUnitStatusDTO(
                        unit.getId(),
                        unit.getBusinessName(),
                        unit.isEnabled(),
                        userRoles,
                        unit.getAttributes().stream()
                                .map(attr -> new BusinessAttributeResponse(attr.getId(), attr.getAttributeName(), attr.getAttributeValue()))
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Page<B2BUnitDTO> searchByCityAndCategory(String city, String categoryName, String searchTerm, Pageable pageable) {
        Page<B2BUnitModel> modelPage = b2bUnitRepository.findByCityAndCategoryName(city, categoryName, pageable);

        List<B2BUnitDTO> dtoList = modelPage.stream()
                .filter(model -> {
                    if (searchTerm == null || searchTerm.isBlank()) return true;
                    return model.getBusinessName() != null &&
                            model.getBusinessName().toLowerCase().contains(searchTerm.toLowerCase());
                })
                .map(this::buildResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, dtoList.size());
    }

    @Override
    public Page<B2BUnitModel> findB2BUnitsWithinRadius(
            double latitude,
            double longitude,
            double radiusInKm,
            String postalCode,
            String searchTerm,
            String categoryName,
            Pageable pageable) {

        boolean hasLatLng = latitude != 0 && longitude != 0;
        boolean hasPostalCode = postalCode != null && !postalCode.isBlank();
        boolean hasSearchTerm = searchTerm != null && !searchTerm.isBlank();

        Page<B2BUnitModel> resultsPage = Page.empty(pageable);

        if (hasLatLng) {
            resultsPage = b2bUnitRepository.findNearbyBusinessesWithCategoryFilter(latitude, longitude, radiusInKm, categoryName, pageable);
        } else if (hasPostalCode) {
            resultsPage = b2bUnitRepository.findByAdminAddressPostalCode(postalCode, pageable);
        }

        if (hasSearchTerm && !resultsPage.isEmpty()) {
            String lowerSearchTerm = searchTerm.toLowerCase();
            List<B2BUnitModel> filteredList = resultsPage.stream()
                    .filter(unit -> unit.getBusinessName() != null &&
                            unit.getBusinessName().toLowerCase().contains(lowerSearchTerm))
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredList, pageable, filteredList.size());
        }

        return resultsPage;
    }

    @Override
    public AddressDTO getAddressByB2BUnitId(Long unitId) {
        AddressModel addressModel = b2bUnitRepository.findBusinessAddressByUnitId(unitId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.ADDRESS_NOT_FOUND,
                        "Address not found for B2B Unit ID: " + unitId));

        AddressDTO addressDTO = new AddressDTO();
        addressPopulator.populate(addressModel, addressDTO);
        return addressDTO;
    }


}
