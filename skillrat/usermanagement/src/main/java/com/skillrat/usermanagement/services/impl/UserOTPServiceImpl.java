package com.skillrat.usermanagement.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.skillrat.usermanagement.model.UserOTPModel;
import com.skillrat.usermanagement.repository.UserOTPRepository;
import com.skillrat.usermanagement.services.UserOTPService;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class UserOTPServiceImpl implements UserOTPService {

    @Autowired
    private UserOTPRepository userOTPRepository;

    @Override
    @Transactional
    public UserOTPModel save(UserOTPModel userOTP) {
        return userOTPRepository.save(userOTP);
    }

    @Override
    public UserOTPModel findByEmailAddressAndOtpType(String emailAddress, String otpType) {
        List<UserOTPModel> emailOtps = userOTPRepository.findByEmailAddressAndOtpType(emailAddress, otpType, Sort.by(Sort.Direction.DESC, "id"));
        if (CollectionUtils.isEmpty(emailOtps)) {
            return null;
        }
        return emailOtps.get(0);
    }

    @Transactional
    public void deleteOTP(UserOTPModel userOtp) {
        userOTPRepository.delete(userOtp);
    }

    @Override
    @Transactional
    public void deleteByPrimaryContactAndOtpType(String primaryContact, String otpType) {
        userOTPRepository.deleteByPrimaryContactAndOtpType(primaryContact, otpType);
    }

    @Override
    public UserOTPModel findByOtpTypeAndPrimaryContact(String otpType, String primaryContact) {
        return userOTPRepository.findByOtpTypeAndPrimaryContact(otpType, primaryContact);
    }

}
