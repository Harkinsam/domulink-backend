package com.domulink.user.controller;

import com.domulink.dto.request.EmailOTPRequest;
import com.domulink.dto.request.LoginRequest;
import com.domulink.dto.request.ResetPasswordRequest;
import com.domulink.dto.request.SignUpRequest;
import com.domulink.dto.response.LoginResponse;
import com.domulink.dto.response.SignUpResponse;
import com.domulink.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/initiate-signup")
    @Operation(summary = "Initiate signup process", description = "Sends an OTP to the email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent to the email"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> userInitialSignUp(@Parameter(description = "Signup request") @Valid @RequestBody SignUpRequest request){
        log.info("Received signup request in controller: {}", request.getPassword());
        String response = authService.initiateSignup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-signup")
    @Operation(summary = "Complete signup process", description = "Confirms the OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signup successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SignUpResponse> userCompleteSignup(@Parameter(description = "Email and OTP") @Valid @RequestBody EmailOTPRequest request){

        SignUpResponse response = authService.completeSignup(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    @Operation(summary = "Login to the app", description = "Validates the email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoginResponse> userLogin(@Parameter(description = "Login request") @Valid @RequestBody LoginRequest loginRequest){
        log.info("Received login request in controller: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authService.login(loginRequest.getEmail(), loginRequest.getPassword()));
    }



    @PostMapping("/refresh-token")
    @Operation(summary = "Refreshes the access token", description = "Uses the refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }


    @PostMapping("/logout")
    @Operation(summary = "Logs out the user", description = "Deletes the refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok("Logged out successfully");
    }
    @GetMapping("/forgot-password")
    @Operation(summary = "Initiates the password reset process", description = "Sends an OTP to the email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent to the email"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> initiatePasswordReset(@Parameter(description = "Email") @RequestParam String email) {
        return ResponseEntity.ok(authService.initiatePasswordResetOtp(email));
    }

    @GetMapping("/verify-password-reset-otp")
    @Operation(summary = "Verifies the password reset OTP", description = "Confirms the OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<String> verifyPasswordRestOtp(@Parameter(description = "Email and OTP") @Valid @RequestBody EmailOTPRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.getEmail(), request.getOtp()));

    }



    @PostMapping("/reset-password")
    @Operation(summary = "Resets the password", description = "Confirms the OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> resetPassword(@Parameter(description = "Email and Password") @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request.getEmail(), request.getPassword()));
    }
}
