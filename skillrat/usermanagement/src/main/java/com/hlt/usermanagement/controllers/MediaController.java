package com.hlt.usermanagement.controllers;

import com.hlt.auth.exception.handling.ErrorCode;
import com.hlt.auth.exception.handling.HltCustomerException;
import com.hlt.commonservice.dto.StandardResponse;
import com.hlt.commonservice.user.UserDetailsImpl;
import com.hlt.usermanagement.dto.MediaDTO;
import com.hlt.usermanagement.services.MediaService;
import com.hlt.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/{b2bUnitId}")
    @PreAuthorize("hasAnyRole('ROLE_USER_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    public StandardResponse<String>uploadMedia(@PathVariable Long b2bUnitId,
                                                @ModelAttribute MediaDTO mediaDTO) {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

        boolean isUserAdmin = loggedInUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER_ADMIN"));

        boolean isRestaurantOwner = loggedInUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_RESTAURANT_OWNER"));

        if (!isUserAdmin && !isRestaurantOwner) {
            throw new HltCustomerException(ErrorCode.ACCESS_DENIED);
        }

        mediaService.uploadMedia(b2bUnitId, mediaDTO);
        return StandardResponse.message("Media uploaded successfully");
    }

}
