package com.skillrat.usermanagement.controllers;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.*;
import com.skillrat.commonservice.enums.ERole;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.azure.service.AwsBlobService;
import com.skillrat.usermanagement.dto.BasicUserDetails;
import com.skillrat.usermanagement.dto.UserUpdateDTO;
import com.skillrat.usermanagement.model.MediaModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.MediaPopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.MediaService;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.utils.AbstractConverter;
import com.skillrat.utils.JTBaseEndpoint;
import com.skillrat.utils.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserEndpoint extends JTBaseEndpoint {

    private final UserService userService;
    private final AwsBlobService awsBlobService;
    private final MediaPopulator mediaPopulator;
    private final B2BUnitRepository b2bUnitRepository;
    private final MediaService mediaService;
    private final UserRepository userRepository;

    private static final String MESSAGE = "message";

    @GetMapping("/find/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        log.info("Fetching customer details for ID: {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping("/userDetails")
    public UserDTO getUserByToken() {
        Long userId = getCurrentUserId();
        log.info("Fetching customer details for ID: {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping("/details/all")
    public List<UserModel> getUserDetailsByIds(@RequestBody List<Long> userIds) {
        return userService.findByIds(userIds);
    }

    // ✅ FIXED: replaced <?> with Map<String, String>
    @PutMapping("/user/role/{role}")
    public ResponseEntity<Map<String, String>> assignRoleToCurrentUser(@PathVariable String role) {
        Long userId = getCurrentUserId();
        try {
            ERole parsedRole = ERole.valueOf(role);
            userService.addUserRole(userId, parsedRole);
            return ResponseEntity.ok(Map.of(MESSAGE, "Role successfully added", "role", parsedRole.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add role: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/userDetails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<String>> updateBasicUserDetails(
            @ModelAttribute @Valid BasicUserDetails details) throws IOException {

        Long userId = getCurrentUserId();
        UserModel userModel = userService.findById(userId);
        if (userModel == null) throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);

        updateUserBasicInfo(details, userModel);
        List<MediaModel> mediaModels = handleMediaFiles(details, userId);
        userService.saveUser(userModel);

        log.info("User and media updated successfully for ID: {}", userId);
        return ResponseEntity.ok(StandardResponse.single("User details updated successfully", "SUCCESS"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable @NotBlank Long id,
                                                      @Valid @RequestBody UserUpdateDTO details) {
        userService.updateUser(details, id);
        log.info("User details updated successfully for ID: {}", id);
        return ResponseEntity.ok(new MessageResponse("Details Updated"));
    }

    @PostMapping("/onboard/user")
    public Long onBoardUser(@Valid @RequestBody BasicOnboardUserDTO dto) {
        return userService.onBoardUser(dto.getFullName(), dto.getPrimaryContact(),
                dto.getUserRoles(), dto.getBusinessId());
    }

    @PostMapping("/onboard-with-credentials")
    public ResponseEntity<Long> onBoardUserWithCredentials(@Valid @RequestBody BasicOnboardUserDTO dto) {
        return ResponseEntity.ok(userService.onBoardUserWithCredentials(dto));
    }

    @PostMapping("/save")
    public UserModel saveUser(@RequestBody UserModel userModel) {
        return userService.saveUser(userModel);
    }

    @DeleteMapping("/contact/{mobileNumber}/role/{role}")
    public ResponseEntity<Map<String, String>> removeUserRole(@PathVariable String mobileNumber,
                                                              @PathVariable ERole role) {
        try {
            userService.removeUserRole(mobileNumber, role);
            return ResponseEntity.ok(Collections.singletonMap(MESSAGE, "Role successfully removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap(MESSAGE, "Failed to remove role: " + e.getMessage()));
        }
    }

    @GetMapping("/contact")
    public LoggedInUser getByPrimaryContact(@Valid @RequestParam String primaryContact) {
        return userService.findByPrimaryContact(primaryContact)
                .map(this::convertToLoggedInUser)
                .orElse(null);
    }

    @PostMapping("/profile/upload")
     public ResponseEntity<MediaDTO> uploadCustomerProfilePicture(@ModelAttribute MultipartFile profilePicture)
        throws IOException {
    Long userId = getCurrentUserId();
    MediaModel mediaModel = awsBlobService.uploadCustomerPictureFile(userId, profilePicture, userId);
    try {
        MediaDTO mediaDTO = (MediaDTO) getConvertedInstance().convert(mediaModel);
        log.info("Profile picture uploaded for user ID {}", userId);
        return ResponseEntity.ok(mediaDTO);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        log.error("Failed to convert MediaModel to MediaDTO", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    @GetMapping("/byRole")
    public List<UserDTO> getUsersByRole(@RequestParam String roleName) {
        return userService.getUsersByRole(roleName);
    }

    @DeleteMapping("/fcmToken")
    public ResponseEntity<MessageResponse> deleteFcmToken() {
        userService.clearFcmToken(getCurrentUserId());
        return ResponseEntity.ok(new MessageResponse("FCM token deleted successfully"));
    }

    @GetMapping("/count/business/{businessId}")
    public ResponseEntity<StandardResponse<Long>> getUserCountByBusinessId(@PathVariable Long businessId) {
        long count = userService.getUserCountByBusinessId(businessId);
        return ResponseEntity.ok(StandardResponse.single("User count fetched successfully", count));
    }

    private Long getCurrentUserId() {
        UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
        if (user == null) throw new HltCustomerException(ErrorCode.UNAUTHORIZED);
        return user.getId();
    }

    private void updateUserBasicInfo(BasicUserDetails details, UserModel userModel) {
        Optional.ofNullable(details.getFullName()).filter(StringUtils::hasText).ifPresent(userModel::setFullName);
        Optional.ofNullable(details.getEmail())
                .filter(StringUtils::hasText)
                .ifPresent(email -> {
                    if (!userService.existsByEmail(email, userModel.getId())) {
                        userModel.setEmail(email);
                    } else throw new HltCustomerException(ErrorCode.EMAIL_ALREADY_IN_USE);
                });
        Optional.ofNullable(details.getFcmToken()).ifPresent(userModel::setFcmToken);
    }

    private List<MediaModel> handleMediaFiles(BasicUserDetails details, Long userId) throws IOException {
        List<MediaModel> mediaModels = new ArrayList<>();

        if (details.getProfilePicture() != null && !details.getProfilePicture().isEmpty()) {
            MediaModel profile = createMediaFromFile(details.getProfilePicture(), userId, "PROFILE_PICTURE");
            mediaService.saveMedia(profile);
        }

        if (details.getMediaFiles() != null) {
            for (MultipartFile file : details.getMediaFiles()) {
                if (!file.isEmpty()) {
                    MediaModel media = createMediaFromFile(file, userId, "USER_PROFILE");
                    mediaService.saveMedia(media);
                    mediaModels.add(media);
                }
            }
        }

        if (details.getMediaUrls() != null) {
            for (String url : details.getMediaUrls()) {
                if (StringUtils.hasText(url)) {
                    MediaModel media = new MediaModel();
                    media.setUrl(url);
                    media.setFileName("external");
                    media.setExtension("url");
                    media.setMediaType("EXTERNAL_LINK");
                    media.setName("External URL: " + url);
                    media.setCreatedBy(userId);
                    media.setCustomerId(userId);
                    media.setCreationTime(new Date());
                    media.setModificationTime(new Date());
                    mediaService.saveMedia(media);
                    mediaModels.add(media);
                }
            }
        }

        if (details.getMedia() != null) {
            for (MediaDTO dto : details.getMedia()) {
                MediaModel media = new MediaModel();
                media.setUrl(dto.getUrl());
                media.setFileName(dto.getName());
                media.setExtension(dto.getExtension());
                media.setMediaType(dto.getMediaType());
                media.setCreationTime(dto.getCreationTime() != null ? dto.getCreationTime() : new Date());
                media.setModificationTime(new Date());
                media.setCreatedBy(userId);
                media.setCustomerId(userId);
                media.setName(StringUtils.hasText(dto.getName()) ? dto.getName() : dto.getUrl());
                media.setDescription(dto.getDescription());
                mediaService.saveMedia(media);
                mediaModels.add(media);
            }
        }
        return mediaModels;
    }

    private MediaModel createMediaFromFile(MultipartFile file, Long userId, String mediaType) throws IOException {
        MediaModel media = awsBlobService.uploadFile(file);
        String fileName = file.getOriginalFilename();
        media.setFileName(fileName);
        media.setName(fileName);
        media.setMediaType(mediaType);
        media.setCustomerId(userId);
        media.setCreatedBy(userId);
        media.setCreationTime(new Date());
        media.setModificationTime(new Date());
        return media;
    }

    private LoggedInUser convertToLoggedInUser(UserModel userModel) {
        LoggedInUser loggedInUser = new LoggedInUser();
        loggedInUser.setId(userModel.getId());
        loggedInUser.setFullName(userModel.getFullName());
        loggedInUser.setPrimaryContact(userModel.getPrimaryContact());
        return loggedInUser;
    }

    @SuppressWarnings("unchecked")
    private AbstractConverter getConvertedInstance() {
        return getConverter(mediaPopulator, MediaDTO.class.getName());
    }
}