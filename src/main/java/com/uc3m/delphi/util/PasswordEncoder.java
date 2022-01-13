package com.uc3m.delphi.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordEncoder {
    private final byte passwordStrength = 10;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(this.passwordStrength, new SecureRandom());
    }

    public String generatePlainPassword() {
        return RandomStringUtils.random(8, true, true);
    }

    public String encodePassword(String inputPassword) {
        return bCryptPasswordEncoder.encode(inputPassword);
    }

    public boolean verifyPassword(String inputPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(inputPassword, encodedPassword);
    }

}
