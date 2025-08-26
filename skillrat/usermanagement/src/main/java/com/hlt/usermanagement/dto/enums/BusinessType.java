package com.hlt.usermanagement.dto.enums;

import com.hlt.auth.exception.handling.ErrorCode;
import com.hlt.auth.exception.handling.HltCustomerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum BusinessType {

    // Education
    COLLEGE("CLG", "College"),
    INSTITUTE("INST", "Institute"),
    UNIVERSITY("UNI", "University"),

    // Corporate / Employment
    COMPANY("COMP", "Company"),

    // Social Sector
    NGO("NGO", "Non-Governmental Organization"),

    OTHER("OTH", "Other");

    private final String code;
    private final String description;

    BusinessType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator
    public static BusinessType fromCode(String code) {
        for (BusinessType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new HltCustomerException(ErrorCode.INVALID_BUSINESS_TYPE);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
