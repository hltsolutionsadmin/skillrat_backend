package com.skillrat.usermanagement.services.impl;


import com.skillrat.commonservice.enums.ERole;
import com.skillrat.usermanagement.model.RoleModel;
import com.skillrat.usermanagement.repository.RoleRepository;
import com.skillrat.usermanagement.services.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleModel findByErole(ERole eRole) {
        Optional<RoleModel> role = roleRepository.findByName(eRole);
        return role.orElse(null);
    }
}
