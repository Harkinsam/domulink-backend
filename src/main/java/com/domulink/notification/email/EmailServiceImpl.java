package com.domulink.notification.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;



    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//            helper.setFrom("no-reply@domulink.com", "DomuLink Support");
            helper.setTo(toEmail);
            helper.setSubject("Your DomuLink Verification Code");

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2>Welcome to DomuLink!</h2>
                        <p>Your verification code is:</p>
                        <h1 style="color: #4CAF50; font-size: 32px;">%s</h1>
                        <p>This code will expire in 5 minutes.</p>
                        <p>If you didn't request this code, please ignore this email.</p>
                        <br>
                        <p>Best regards,<br>DomuLink Team</p>
                    </div>
                    """.formatted(otp);

            helper.setText(htmlContent, true); // true indicates HTML content

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendRenewalDisabledEmail(String toEmail, String tenantName, String propertyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Rental Renewal Not Allowed");

            String htmlContent = """
                        <div style="font-family: Arial, sans-serif; padding: 20px;">
                            <h2>Hello %s,</h2>
                            <p>We regret to inform you that the renewal for your rental at:</p>
                            <p><strong>%s</strong></p>
                            <p>has been disabled by the landlord.</p>
                            <p>If you have any questions, please contact your landlord or our support team.</p>
                            <br>
                            <p>Best regards,<br>DomuLink Team</p>
                        </div>
                    """.formatted(tenantName, propertyName);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Renewal disabled email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send renewal disabled email to: {}", toEmail, e);
        }
    }


    @Override
    public void sendWelcomeEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Welcome to DomuLink!");

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2>Welcome to DomuLink!</h2>
                        <p>We're excited to have you join our platform.</p>
                        <p>You can now search for rentals, manage your property, and more—all in one place.</p>
                        <p>If you have any questions, feel free to reach out to our support team.</p>
                        <br>
                        <p>Best regards,<br>DomuLink Team</p>
                    </div>
                    """;

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
}