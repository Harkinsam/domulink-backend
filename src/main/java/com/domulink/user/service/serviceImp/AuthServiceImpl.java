package com.domulink.user.service.serviceImp;

import com.domulink.security.CustomUserDetails;
import com.domulink.security.CustomUserDetailsService;
import com.domulink.dto.request.SignUpRequest;
import com.domulink.dto.response.LoginResponse;
import com.domulink.dto.response.SignUpResponse;
import com.domulink.exception.InvalidTokenException;
import com.domulink.exception.UserAlreadyExistsException;
import com.domulink.exception.UserNotFoundException;
import com.domulink.notification.email.EmailService;
import com.domulink.entity.User;
import com.domulink.repository.UserRepository;
import com.domulink.user.service.AuthService;
import com.domulink.util.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${application.security.jwt.refresh-token.expiration-time}")
    private long refreshExpiration;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtService;
    private final RedisService redisService;
    private final EmailService emailService;
    private final CustomUserDetailsService customUserDetailsService;



    public String initiateSignup(SignUpRequest signUpRequest) {
        log.info("Processing signup request in service: {}", signUpRequest);
        Optional<User> existingUser = userRepository.findUserByEmail(signUpRequest.getEmail());
        if(existingUser.isPresent()){
            throw new UserAlreadyExistsException("User already exists with email " + signUpRequest.getEmail());
        }

        String otp = generateOtp();

        redisService.storeSignupData(signUpRequest, otp);
        emailService.sendOtpEmail(signUpRequest.getEmail(), otp);
        log.info("Signup request stored in Redis for email: {}", signUpRequest.getEmail());
        log.info("OTP for email: {} is: {}",signUpRequest.getEmail(), otp);
        log.info("OTP sent to email: {}", signUpRequest.getEmail());
        return "otp successfully sent to email. Enter otp to complete signup";
    }


    public SignUpResponse completeSignup(String email, String otp) {
        log.info("Processing complete signup request in service: {} and otp: {}", email, otp);
        String storedOtp = redisService.getOtp(email);
        SignUpRequest storedSignupRequest = redisService.getSignupRequest(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            log.warn("Invalid OTP for email: {}", email);
            return new SignUpResponse(email, "Invalid OTP for email: " + email);
        }

        log.info("Creating user with signup request: {}", storedSignupRequest);
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setPhoneNumber(storedSignupRequest.getPhoneNumber());
        user.setEmail(storedSignupRequest.getEmail());
        user.setFirstName(storedSignupRequest.getFirstName());
        user.setLastName(storedSignupRequest.getLastName());
        user.setPassword(passwordEncoder.encode(storedSignupRequest.getPassword()));
//        user.setVerified(true);
        user.setRole(storedSignupRequest.getRole());
        userRepository.save(user);

        redisService.deleteSignupData(email);
        log.info("Signup completed successfully for email: {}", email);
        emailService.sendWelcomeEmail(user.getEmail());
        return new SignUpResponse(email, "Signup completed successfully for email: " + email);

    }



    public LoginResponse login(String email, String password) {
        log.info("Processing login request in service: {}, {}", email, password);

        Authentication authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

         String accessToken = jwtService.generateToken(customUserDetails);
         String refreshToken = jwtService.generateRefreshToken(customUserDetails);
        log.info("Access token generated: {}", accessToken);
        log.info("Refresh token generated: {}", refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh-token")
                .maxAge(refreshExpiration/1000)
                .build();

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("Login successful for email: {}", email);
        return new LoginResponse("Login successful", accessToken, "Bearer", email);
    }

    public LoginResponse refreshToken(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        String email = jwtService.extractEmail(refreshToken);
        log.info("Extracted email from refresh token: {}", email);
        CustomUserDetails customUserDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(email);
        if (jwtService.isTokenValid(refreshToken, customUserDetails)) {
            String newAccessToken = jwtService.generateToken(customUserDetails);
            log.info("Access token refreshed successfully for email: {}", email);

            return new LoginResponse("Access token refreshed successfully", newAccessToken,  "Bearer", email);
        }

        throw new InvalidTokenException("Invalid refresh token");
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new InvalidTokenException("Refresh token not found");
    }


    public void logout(HttpServletResponse response) {
        log.info("Logging out user...");
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/logout")
                .maxAge(0) // Expires immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("Logged out successfully");
    }


    public String resetPassword(String email, String newPassword) {
        Optional<User> existingUser = userRepository.findUserByEmail(email);
        if(existingUser.isEmpty()){
            throw new UserNotFoundException("User not found with email " + email);
        }
        User user = existingUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset successfully for email: {}", email);
        return "Password reset successfully for email: " + email;
    }


    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%d", 100000 + secureRandom.nextInt(900000));
    }


    public String initiatePasswordResetOtp(String email) {
        Optional<User> existingUser = userRepository.findUserByEmail(email);

        if(existingUser.isPresent()){
            String otp = generateOtp();
            redisService.storeResetPasswordOtp(email, otp);
            emailService.sendOtpEmail(email, otp);
            log.info("OTP for email: {} is: {}",email, otp);
            log.info("OTP sent to email: {}", email);
            return "If your email exists, you will receive an OTP to reset your password";
        }

        return "If your email exists in our system, you will receive an OTP to reset your password.";
    }

    public String verifyOtp(String email, String otp) {
        String storedOtp = redisService.getResetPasswordOtp(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            log.warn("Invalid OTP for email: {}", email);
            return "Invalid OTP for email: " + email;
        }
        redisService.deleteResetPasswordOtp(email);
        log.info("OTP verified successfully for email: {}", email);
        return "OTP verified successfully for email: " + email;
    }
}