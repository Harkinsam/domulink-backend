package com.domulink.user.service;

import com.domulink.dto.response.LoginResponse;
import com.domulink.dto.request.SignUpRequest;
import com.domulink.dto.response.SignUpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    String initiateSignup(SignUpRequest request);

    SignUpResponse completeSignup(String email, String otp);

    LoginResponse login(String email, String password);
    LoginResponse refreshToken(HttpServletRequest request);
    void logout(HttpServletResponse response);
    String resetPassword(String email, String newPassword);
    String initiatePasswordResetOtp(String email);
    String verifyOtp(String email, String otp);
}
