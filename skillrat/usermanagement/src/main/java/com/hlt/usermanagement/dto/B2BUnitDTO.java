package com.hlt.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hlt.usermanagement.dto.enums.BusinessType;
import com.hlt.usermanagement.dto.response.BusinessAttributeResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class B2BUnitDTO {

    private Long id;

    private String businessName;

    private BusinessType type;

    private String businessCode;

    private String contactNumber;

    private String email;

    private String websiteUrl;

    private boolean enabled;

    private boolean temporarilyClosed;

    private Double businessLatitude;

    private Double businessLongitude;

    private Long categoryId;

    private String categoryName;

    private LocalDateTime creationDate;

    private UserDTO admin;

    private AddressDTO businessAddress;

    private Set<BusinessAttributeResponse> attributes;

    private List<MultipartFile> mediaFiles;

    private List<String> mediaUrls;

    private List<MediaDTO> mediaList;

}
