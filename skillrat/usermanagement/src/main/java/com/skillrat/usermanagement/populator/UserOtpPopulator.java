package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.UserOTPDTO;
import com.skillrat.usermanagement.model.UserOTPModel;
import com.skillrat.utils.Populator;

import org.springframework.stereotype.Component;


@Component
public class UserOtpPopulator implements Populator<UserOTPModel, UserOTPDTO> {

    @Override
    public void populate(UserOTPModel source, UserOTPDTO target) {
        target.setId(source.getId());
        target.setCreationTime(source.getCreationTime());
        target.setOtpType(source.getOtpType());
        target.setOtp(source.getOtp());
    }

}
