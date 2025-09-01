package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.UserServiceAdapter;
import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.*;
import com.skillrat.commonservice.enums.ERole;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.UserUpdateDTO;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.repository.*;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.utils.SecurityUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor // ✅ Still the best way for DI
public class UserServiceImpl implements UserService, UserServiceAdapter {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CaffeineCacheManager cacheManager;
    private final MediaRepository mediaRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserModel saveUser(UserModel userModel) {
        try {
            return userRepository.save(userModel);
        } catch (Exception ex) {
            log.error("Failed to save user: {}", userModel, ex);
            throw ex;
        }
    }

    @Override
    public Long onBoardUserWithCredentials(BasicOnboardUserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new HltCustomerException(ErrorCode.USER_ALREADY_EXISTS);
        }

        UserModel user = new UserModel();
        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setPrimaryContact(dto.getPrimaryContact());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(fetchRoles(dto.getUserRoles()));
        if (dto.getBusinessId() != null) {
            user.setB2bUnit(findB2BUnitById(dto.getBusinessId()));
        }

        return saveUser(user).getId();
    }

    @Override
    public void updateUser(UserUpdateDTO details, Long userId) {
        UserDetailsImpl currentUser = SecurityUtils.getCurrentUserDetails();
        UserModel user = getUserByIdOrThrow(currentUser.getId());

        if (!Objects.equals(userId, user.getId())) {
            throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
        }

        if (existsByEmail(details.getEmail(), userId)) {
            throw new HltCustomerException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        user.setEmail(details.getEmail());
        user.setFullName(details.getFullName());
        saveUser(user);
    }

    @Override
    public Long onBoardUser(String fullName, String mobileNumber, Set<ERole> userRoles, Long b2bUnitId) {
        return findByPrimaryContact(mobileNumber)
                .map(UserModel::getId)
                .orElseGet(() -> {
                    UserModel user = new UserModel();
                    user.setPrimaryContact(mobileNumber);
                    user.setRoles(fetchRoles(userRoles));
                    user.setFullName(fullName);
                    if (b2bUnitId != null) {
                        user.setB2bUnit(findB2BUnitById(b2bUnitId));
                    }
                    return saveUser(user).getId();
                });
    }

    @Override
    public void addUserRole(Long userId, ERole userRole) {
        UserModel user = getUserByIdOrThrow(userId);
        RoleModel role = getRoleByEnum(userRole);

        if (user.getRoles().add(role)) {
            saveUser(user);
        }
    }

    @Override
    public void removeUserRole(String mobileNumber, ERole userRole) {
        UserModel user = findByPrimaryContact(mobileNumber)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
        RoleModel role = getRoleByEnum(userRole);

        if (!user.getRoles().remove(role)) {
            throw new HltCustomerException(ErrorCode.ROLE_NOT_FOUND);
        }

        saveUser(user);
    }

    @Override
    @Transactional
    public UserDTO getUserById(Long userId) {
        UserModel user = getUserByIdOrThrow(userId);
        UserDTO dto = convertToUserDto(user);

        dto.setMedia(
                mediaRepository.findByCustomerId(userId)
                        .stream()
                        .map(this::convertToMediaDto)
                        .toList()
        );

        return dto;
    }

    @Override
    public UserModel findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<UserModel> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public UserModel findByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        String emailHash = DigestUtils.sha256Hex(email.trim().toLowerCase());
        return userRepository.findByEmailHash(emailHash).orElse(null);
    }

    @Override
    public Optional<UserModel> findByPrimaryContact(String primaryContact) {
        return userRepository.findByPrimaryContactHash(DigestUtils.sha256Hex(primaryContact));
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleName) {
        RoleModel role = getRoleByEnum(ERole.valueOf(roleName.toUpperCase()));
        return userRepository.findByRolesContaining(role)
                .stream()
                .map(this::convertToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void clearFcmToken(Long userId) {
        UserModel user = getUserByIdOrThrow(userId);
        user.setFcmToken(null);
        saveUser(user);
    }

    @Override
    public long getUserCountByBusinessId(Long businessId) {
        return userRepository.countUsersByBusinessId(businessId);
    }

    @Override
    public Optional<UserModel> findByUsername(@NotBlank String username) {
        return userRepository.findByUsername(username);
    }

    private void updateCache(Long userId, UserModel userModel) {
        Cache userCache = cacheManager.getCache("users");
        if (userCache != null) {
            userCache.put(userId, convertToUserDto(userModel));
        }
    }

    private RoleModel getRoleByEnum(ERole role) {
        return roleRepository.findByName(role)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.ROLE_NOT_FOUND));
    }

    private B2BUnitModel findB2BUnitById(Long id) {
        return b2bUnitRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private Set<RoleModel> fetchRoles(Set<ERole> roles) {
        return roleRepository.findByNameIn(roles);
    }

    private UserModel getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }



    private MediaDTO convertToMediaDto(MediaModel media) {
        MediaDTO dto = new MediaDTO();
        dto.setId(media.getId());
        dto.setUrl(media.getUrl());
        dto.setName(media.getFileName());
        dto.setDescription(media.getDescription());
        dto.setExtension(media.getExtension());
        dto.setCreationTime(media.getCreationTime());
        dto.setMediaType(media.getMediaType());
        return dto;
    }

    private B2BUnitDTO convertToB2BDTO(B2BUnitModel unit) {
        B2BUnitDTO dto = new B2BUnitDTO();
        dto.setId(unit.getId());
        dto.setBusinessName(unit.getBusinessName());
        dto.setEnabled(unit.isEnabled());
        return dto;
    }

    public UserDTO convertToUserDto(UserModel user) {
        // map roles safely without assuming a constructor exists
        Set<com.skillrat.commonservice.dto.Role> roles = user.getRoles().stream().map(r -> {
            com.skillrat.commonservice.dto.Role rd = new com.skillrat.commonservice.dto.Role();
            rd.setId(r.getId());
            rd.setName(r.getName());
            return rd;
        }).collect(java.util.stream.Collectors.toSet());

        String profilePicture = Optional.ofNullable(
                        mediaRepository.findByCustomerIdAndMediaType(user.getId(), "PROFILE_PICTURE"))
                .map(MediaModel::getUrl)
                .orElse(null);

        B2BUnitDTO b2bUnit = Optional.ofNullable(user.getB2bUnit())
                .map(this::convertToB2BDTO)
                .orElseGet(() -> b2bUnitRepository.findByOwner(user)
                        .map(this::convertToB2BDTO)
                        .orElse(null));

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPrimaryContact(user.getPrimaryContact());
        dto.setEmail(user.getEmail());
        dto.setToken(user.getFcmToken());
        dto.setUsername(user.getUsername());
        dto.setGender(user.getGender());
        dto.setProfilePicture(profilePicture);
        dto.setRoles(roles);
        dto.setPassword(user.getPassword());
        dto.setB2bUnit(b2bUnit);
        // dto.setMedia(...) is set separately in getUserById(...)
        return dto;
    }



    public Boolean existsByEmail(String email, Long userId) {
        return userRepository.existsByEmailAndNotUserId(email, userId);
    }
}
