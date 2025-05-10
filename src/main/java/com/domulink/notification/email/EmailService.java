package com.domulink.notification.email;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
    void sendRenewalDisabledEmail(String toEmail, String tenantName, String propertyName);
    void sendWelcomeEmail(String toEmail);

}
