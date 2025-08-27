package com.skillrat.usermanagement.utils;

import org.apache.commons.lang.RandomStringUtils;

public class PasswordUtil {

    public static String generateRandomPassword(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
