package com.skillrat.usermanagement.controllers;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hlt.customerservices.CustomerIntegrationService;
import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.customerservices.impl.CustomerIntegrationServiceImpl;
import com.skillrat.usermanagement.dto.UserOTPDTO;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.model.UserOTPModel;
import com.skillrat.usermanagement.services.UserOTPService;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.utils.SRBaseEndpoint;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/auth/jtuserotp")
@Slf4j
@RequiredArgsConstructor
public class UserOTPController extends SRBaseEndpoint {

    private final UserOTPService userOTPService;
    private final UserService userService;

    @Value("${otp.trigger}")
    private boolean triggerOtp;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DecimalFormat OTP_FORMAT = new DecimalFormat("000000");

    @PostMapping("/trigger/sign-in")
    public ResponseEntity<?> signIn(
            @Valid @RequestBody UserOTPDTO userOtpDto,
            @RequestParam(defaultValue = "true") boolean triggerOtp) throws IOException {

        log.info("Entering sign-in with Primary Contact: {}, OTP Type: {}",
                userOtpDto.getPrimaryContact(), userOtpDto.getOtpType());

        Optional<UserModel> user = userService.findByPrimaryContact(userOtpDto.getPrimaryContact());
        if (user.isEmpty()) {
            throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
        }

        userOTPService.deleteByPrimaryContactAndOtpType(
                userOtpDto.getPrimaryContact(), userOtpDto.getOtpType());

        UserOTPModel userOtp = generateAndSaveOtp(userOtpDto);
        UserOTPDTO userOTPDTO = new UserOTPDTO(userOtp.getOtp(), userOtp.getCreationTime());

        CustomerIntegrationService jtCustomerIntegration = new CustomerIntegrationServiceImpl();

        if (triggerOtp) {
            log.debug("Triggering OTP for Primary Contact: {}", userOtp.getPrimaryContact());
            jtCustomerIntegration.triggerSMS(userOtp.getPrimaryContact(), userOtp.getOtp());
            userOTPDTO.setOtp(null);
        }

        log.info("Returning response for Primary Contact: {}", userOtpDto.getPrimaryContact());
        return new ResponseEntity<>(userOTPDTO, HttpStatus.OK);
    }

    @PostMapping("/trigger/otp")
    public ResponseEntity<UserOTPDTO> newUserSignUp(@Valid @RequestBody UserOTPDTO userOtpDto) throws IOException {

        log.info("Entering newUserSignUp with Primary Contact: {}, OTP Type: {}",
                userOtpDto.getPrimaryContact(), userOtpDto.getOtpType());

        userOTPService.deleteByPrimaryContactAndOtpType(
                userOtpDto.getPrimaryContact(), userOtpDto.getOtpType());

        UserOTPModel userOtp = generateAndSaveOtp(userOtpDto);
        UserOTPDTO userOTPDTO = new UserOTPDTO(userOtp.getOtp(), userOtp.getCreationTime());

        CustomerIntegrationService jtCustomerIntegration = new CustomerIntegrationServiceImpl();
        log.info("Returning response for triggerOtp value: {}", triggerOtp);

        if (triggerOtp) {
            log.debug("Triggering OTP for Primary Contact: {}", userOtp.getPrimaryContact());
            jtCustomerIntegration.triggerSMS(userOtp.getPrimaryContact(), userOtp.getOtp());
            userOTPDTO.setOtp(null); // mask OTP if triggered
        }

        log.info("Returning response for Primary Contact: {}", userOtpDto.getPrimaryContact());
        return ResponseEntity.ok(userOTPDTO);
    }


    private UserOTPModel generateAndSaveOtp(UserOTPDTO userOTPDTO) {
        log.debug("Populating JTUserOTPModel for Primary Contact: {}", userOTPDTO.getPrimaryContact());

        UserOTPModel jtUserOTP = new UserOTPModel();
        jtUserOTP.setChannel(userOTPDTO.getChannel());
        jtUserOTP.setCreationTime(new Date());
        jtUserOTP.setPrimaryContact(userOTPDTO.getPrimaryContact());
        jtUserOTP.setEmailAddress(userOTPDTO.getEmailAddress());
        jtUserOTP.setOtpType(userOTPDTO.getOtpType());

        Map<String, String> staticOtps = Map.of(
                "9100881724", "123123",
                "9700020630", "291120"
        );

        String primaryContact = userOTPDTO.getPrimaryContact();
        String otp = staticOtps.get(primaryContact);

        if (otp != null) {
            jtUserOTP.setOtp(otp);
            log.debug("Static OTP assigned for Primary Contact: {}", primaryContact);
        } else {
            otp = generateOtp();
            jtUserOTP.setOtp(otp);
            log.debug("Generated OTP for Primary Contact: {}", primaryContact);
        }

        return userOTPService.save(jtUserOTP);
    }

    private String generateOtp() {
        int number = SECURE_RANDOM.nextInt(1_000_000);
        return OTP_FORMAT.format(number);
    }
}
