package com.skillrat.usermanagement.dto;

import com.skillrat.usermanagement.dto.enums.BusinessType;
import com.skillrat.usermanagement.dto.response.BusinessAttributeResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class B2BUnitDTO {

    private Long id;

    @NotNull(message = "Admin User  is mandatory")
    private UserDTO adminUser;

    @NotBlank(message = "Business name is required")
    @Size(max = 150, message = "Business name cannot exceed 150 characters")
    private String businessName;

    @NotBlank(message = "Business code is required")
    @Size(max = 50, message = "Business code cannot exceed 50 characters")
    private String businessCode;

    @NotNull(message = "Business type is required")
    private BusinessType type;

    @NotNull(message = "Category  is required")
	private String categoryName;

    @NotBlank(message = "Contact number is required")
    @Size(max = 20, message = "Contact number cannot exceed 20 characters")
    @Pattern(regexp = "^[0-9+\\-() ]{6,20}$",
            message = "Contact number contains invalid characters")
    private String contactNumber;

    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be <= 90")
    private BigDecimal businessLatitude;

    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be <= 180")
    private BigDecimal businessLongitude;

    private Set<BusinessAttributeResponse> attributes;


    @NotNull(message = "Business address is required")
    @Valid
    private AddressDTO businessAddress;

    @NotNull(message = "Enabled flag is required")
    private Boolean enabled;

    @NotNull(message = "Temporarily closed flag is required")
    private Boolean temporarilyClosed;

    private PlacementCellDTO placementCell;
}
