package com.skillrat.usermanagement.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public UserModel loadUserByPrimaryContact(String primaryContact) {
        try {
            Optional<UserModel> optionalUser = userRepository.findByPrimaryContact(primaryContact);
            return optionalUser.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }


}
