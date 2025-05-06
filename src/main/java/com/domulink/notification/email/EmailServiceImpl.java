package com.domulink.notification.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


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

    @Override
    public void sendWelcomeEmail(String toEmail) {

    }
}