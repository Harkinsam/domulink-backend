package com.domulink.notification.email;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
    void sendWelcomeEmail(String toEmail);

}
