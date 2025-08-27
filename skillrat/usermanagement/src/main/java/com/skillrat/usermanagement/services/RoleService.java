package com.skillrat.usermanagement.services;


import com.skillrat.commonservice.enums.ERole;
import com.skillrat.usermanagement.model.RoleModel;

public interface RoleService {
    RoleModel findByErole(ERole eRole);
}
