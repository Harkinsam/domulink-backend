package com.domulink.payment.controller;

import com.domulink.dto.request.PayStackPaymentDTO;
import com.domulink.dto.request.PayStackWebhookDTO;
import com.domulink.dto.response.PayStackResponse;
import com.domulink.payment.service.PaymentService;
import com.domulink.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
@Slf4j
public class paymentController {

    private final PaymentService paymentService;


    @PostMapping("/paystack")
    public ResponseEntity<PayStackResponse> payForRent(@RequestBody PayStackPaymentDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userEmail = customUserDetails.getUsername();

        return ResponseEntity.ok(paymentService.initializePayment(request.getAmount(),userEmail,request.getDescription()));
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody PayStackWebhookDTO webhookPayload) {
        log.info("Received webhook from Paystack: {}", webhookPayload.getEvent());

        try {
            // Process the webhook
            paymentService.handleWebhook(webhookPayload);

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
        }
    }
}
