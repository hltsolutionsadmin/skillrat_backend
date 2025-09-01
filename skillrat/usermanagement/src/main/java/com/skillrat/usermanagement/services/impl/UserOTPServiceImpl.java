package com.skillrat.usermanagement.services.impl;

import com.skillrat.usermanagement.model.UserOTPModel;
import com.skillrat.usermanagement.repository.UserOTPRepository;
import com.skillrat.usermanagement.services.UserOTPService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserOTPServiceImpl implements UserOTPService {

    private final UserOTPRepository userOTPRepository;

    @Override
    public UserOTPModel save(UserOTPModel userOTP) {
        return userOTPRepository.save(userOTP);
    }

    @Override
    public UserOTPModel findByEmailAddressAndOtpType(String emailAddress, String otpType) {
        List<UserOTPModel> emailOtps =
                userOTPRepository.findByEmailAddressAndOtpType(
                        emailAddress, otpType,
                        Sort.by(Sort.Direction.DESC, "id")
                );

        return CollectionUtils.isEmpty(emailOtps) ? null : emailOtps.get(0);
    }

    @Override
    public void deleteOTP(UserOTPModel userOtp) {
        userOTPRepository.delete(userOtp);
    }

    @Override
    public void deleteByPrimaryContactAndOtpType(String primaryContact, String otpType) {
        userOTPRepository.deleteByPrimaryContactAndOtpType(primaryContact, otpType);
    }

    @Override
    public UserOTPModel findByOtpTypeAndPrimaryContact(String otpType, String primaryContact) {
        return userOTPRepository.findByOtpTypeAndPrimaryContact(otpType, primaryContact);
    }
}
