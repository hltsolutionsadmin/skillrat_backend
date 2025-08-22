package com.hlt.usermanagement.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String email;
    private List<String> roles;
    private String primaryContact;
    private List<Long> businessIds;  // Changed to List<Long>

    public JwtResponse(String accessToken, Long id, String primaryContact, String email, List<String> roles,
                       String refreshToken, List<Long> businessIds) {
        this.token = accessToken;
        this.id = id;
        this.primaryContact = primaryContact;
        this.email = email;
        this.roles = roles;
        this.refreshToken = refreshToken;
        this.businessIds = businessIds;
    }
}
