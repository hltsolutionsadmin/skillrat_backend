package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.BasicOnboardUserDTO;
import com.skillrat.commonservice.dto.Role;
import com.skillrat.commonservice.enums.ERole;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.azure.service.AzureBlobService;
import com.skillrat.usermanagement.dto.AddressDTO;
import com.skillrat.usermanagement.dto.B2BUnitDTO;
import com.skillrat.usermanagement.dto.B2BUnitStatusDTO;
import com.skillrat.usermanagement.dto.UserDTO;
import com.skillrat.usermanagement.dto.request.B2BUnitRequest;
import com.skillrat.usermanagement.dto.request.ProductAttributeRequest;
import com.skillrat.usermanagement.dto.response.B2BUnitListResponse;
import com.skillrat.usermanagement.dto.response.BusinessAttributeResponse;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.populator.AddressPopulator;
import com.skillrat.usermanagement.populator.B2BUnitPopulator;
import com.skillrat.usermanagement.populator.UserPopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.BusinessCategoryRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.B2BUnitService;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.utils.SRBaseEndpoint;
import com.skillrat.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class B2BUnitServiceImpl extends SRBaseEndpoint implements B2BUnitService {

    private final BusinessCategoryRepository categoryRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final B2BUnitPopulator b2bUnitPopulator;
    private final UserRepository userRepository;
    private final UserPopulator userPopulator;
    private final UserService userService;
    private final AddressPopulator addressPopulator;

    @Override
    @Transactional
    public B2BUnitDTO createOrUpdate(B2BUnitRequest request) throws IOException {
        //  Onboard admin user and fetch UserModel
        BasicOnboardUserDTO basicOnboardUserDTO = buildBasicOnboardUserDTO(request);
        Long onboardedUserId = userService.onBoardUserWithCredentials(basicOnboardUserDTO);
        UserModel currentUser = fetchCurrentUser(onboardedUserId);

        //  Fetch existing or create new B2B unit
        B2BUnitModel unit = fetchOrCreateUnit(request, currentUser);

        //Populate all fields
        populateBasicDetails(unit, request);
        populateAddress(unit, request);
        populateCategory(unit, request);
        populateAttributes(unit, request);

        // Save and return DTO
        B2BUnitModel saved = b2bUnitRepository.save(unit);
        return buildResponseDTO(saved);
    }

    private BasicOnboardUserDTO buildBasicOnboardUserDTO(B2BUnitRequest request) {
        Set<ERole> roles = Set.of(ERole.ROLE_BUSINESS_ADMIN);

        return BasicOnboardUserDTO.builder()
                .username(request.getAdminUsername())
                .email(request.getAdminEmail())
                .fullName(request.getAdminFullName())
                .primaryContact(request.getAdminMobile())
                .password(request.getAdminPassword())
                .userRoles(roles)
                .businessId(request.getBusinessId())
                .build();
    }

    private B2BUnitModel fetchOrCreateUnit(B2BUnitRequest request, UserModel currentUser) {
        return Optional.ofNullable(request.getBusinessCode())
                .map(code -> b2bUnitRepository.findByBusinessCode(code)
                        .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND)))
                .orElseGet(() -> {
                    B2BUnitModel unit = new B2BUnitModel();
                    unit.setAdmin(currentUser);
                    unit.setBusinessCode(generateBusinessCode());
                    return unit;
                });
    }

    private void populateBasicDetails(B2BUnitModel unit, B2BUnitRequest request) {
        Optional.ofNullable(request.getBusinessName()).ifPresent(unit::setBusinessName);
        Optional.ofNullable(request.getContactNumber()).ifPresent(unit::setContactNumber);
        Optional.ofNullable(request.getLatitude()).ifPresent(unit::setBusinessLatitude);
        Optional.ofNullable(request.getLongitude()).ifPresent(unit::setBusinessLongitude);
        Optional.ofNullable(request.getBusinessType()).ifPresent(unit::setType);
        Optional.ofNullable(request.getBusinessCode()).ifPresent(unit::setBusinessCode);
        Optional.ofNullable(request.getEnabled()).ifPresent(unit::setEnabled);
        Optional.ofNullable(request.getTemporarilyClosed()).ifPresent(unit::setTemporarilyClosed);
    }

    private void populateAddress(B2BUnitModel unit, B2BUnitRequest request) {
        AddressModel address = Optional.ofNullable(unit.getBusinessAddress()).orElseGet(AddressModel::new);

        Optional.ofNullable(request.getAddressLine1()).ifPresent(address::setAddressLine1);
        Optional.ofNullable(request.getStreet()).ifPresent(address::setStreet);
        Optional.ofNullable(request.getCity()).ifPresent(address::setCity);
        Optional.ofNullable(request.getState()).ifPresent(address::setState);
        Optional.ofNullable(request.getCountry()).ifPresent(address::setCountry);
        Optional.ofNullable(request.getPostalCode()).ifPresent(address::setPostalCode);
        Optional.ofNullable(request.getLatitude()).ifPresent(address::setLatitude);
        Optional.ofNullable(request.getLongitude()).ifPresent(address::setLongitude);

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
        request.getAttributes().stream().map(attr -> {
            BusinessAttributeModel model = new BusinessAttributeModel();
            model.setAttributeName(attr.getAttributeName());
            model.setAttributeValue(attr.getAttributeValue());
            model.setB2bUnit(unit);
            return model;
        }).forEach(attributes::add);
    }

    private B2BUnitDTO buildResponseDTO(B2BUnitModel savedModel) {
        B2BUnitDTO dto = b2bUnitPopulator.toDTO(savedModel);

        Optional.ofNullable(savedModel.getAdmin()).ifPresent(admin -> {
            UserDTO userDTO = new UserDTO();
            userPopulator.populate(admin, userDTO);
            dto.setAdminUser(userDTO);
        });

        return dto;
    }

    private String generateBusinessCode() {
        return "BUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private UserModel fetchCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
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

        if (model.getCategory() != null) {
            response.setCategoryName(model.getCategory().getName());
        }

        if (model.getAdmin() != null) {
            response.setUserId(model.getAdmin().getId());
        }

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

        B2BUnitDTO dto = new B2BUnitDTO();
        b2bUnitPopulator.populate(model, dto);
        dto.setEnabled(Boolean.TRUE.equals(model.getEnabled()));

        return dto;
    }


    @Override
    public List<B2BUnitStatusDTO> getBusinessNameAndApprovalStatusForLoggedInUser() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();

        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<Role> userRoles = userModel.getRoles().stream()
                .map(role -> new Role(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        List<B2BUnitModel> b2BUnits = b2bUnitRepository.findByAdminId(userId);

        if (b2BUnits.isEmpty()) {
            return List.of(B2BUnitStatusDTO.rolesOnly(userRoles));
        }

        return b2BUnits.stream()
                .map(b2BUnit -> mapToStatusDTO(b2BUnit, userRoles))
                .toList();

    }
    private B2BUnitStatusDTO mapToStatusDTO(B2BUnitModel b2BUnit, Set<Role> userRoles) {
        Set<BusinessAttributeResponse> attributes = b2BUnit.getAttributes().stream()
                .map(attr -> new BusinessAttributeResponse(
                        attr.getId(),
                        attr.getAttributeName(),
                        attr.getAttributeValue()
                ))
                .collect(Collectors.toSet());

        return new B2BUnitStatusDTO(
                b2BUnit.getId(),
                b2BUnit.getBusinessName(),
                b2BUnit.getEnabled(),
                userRoles,
                attributes
        );
    }

    @Override
    @Transactional
    public B2BUnitDTO approveBusiness(Long businessId, Long adminUserId) {
        B2BUnitModel business = b2bUnitRepository.findById(businessId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));

        if (Boolean.TRUE.equals(business.getEnabled())) {
            throw new HltCustomerException(ErrorCode.BUSINESS_ALREADY_APPROVED);
        }

        business.setEnabled(Boolean.TRUE);
        business.setTemporarilyClosed(Boolean.FALSE);

        b2bUnitRepository.save(business);

        B2BUnitDTO dto = new B2BUnitDTO();
        b2bUnitPopulator.populate(business, dto);
        return dto;
    }

    @Override
    public Page<B2BUnitDTO> searchByCityAndCategory(String city, String categoryName, String searchTerm, Pageable pageable) {
        Page<B2BUnitModel> modelPage = b2bUnitRepository.findByCityAndCategoryName(city, categoryName, pageable);

        List<B2BUnitModel> filteredModels = modelPage.stream()
                .filter(model -> {
                    if (searchTerm == null || searchTerm.isBlank()) return true;
                    String businessName = model.getBusinessName();
                    return businessName != null && businessName.toLowerCase().contains(searchTerm.toLowerCase());
                })
                .toList();
        List<B2BUnitDTO> dtoList = filteredModels.stream()
                .map(model -> {
                    B2BUnitDTO dto = new B2BUnitDTO();
                    b2bUnitPopulator.populate(model, dto);
                    if (model.getAdmin() != null) {
                        UserDTO userDTO = new UserDTO();
                        userPopulator.populate(model.getAdmin(), userDTO, false);
                        dto.setAdminUser(userDTO);
                    }
                    return dto;
                })
                .toList();

        return new PageImpl<>(dtoList, pageable, filteredModels.size());
    }

    public AddressDTO getAddressByB2BUnitId(Long unitId) {
        AddressModel addressModel = b2bUnitRepository.findBusinessAddressByUnitId(unitId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.ADDRESS_NOT_FOUND, "Address not found for B2B Unit ID: " + unitId));
        AddressDTO addressDTO = new AddressDTO();
        addressPopulator.populate(addressModel, addressDTO);
        return addressDTO;
    }


}