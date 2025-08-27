package com.skillrat.usermanagement.services;


import com.skillrat.usermanagement.model.UserOTPModel;

public interface UserOTPService {
    UserOTPModel save(UserOTPModel userOTP);

    UserOTPModel findByEmailAddressAndOtpType(String emailAddress, String otpType);

    void deleteOTP(UserOTPModel userOtp);

    void deleteByPrimaryContactAndOtpType(String primaryContact, String otpType);

    UserOTPModel findByOtpTypeAndPrimaryContact(String otpType, String primaryContact);
}
