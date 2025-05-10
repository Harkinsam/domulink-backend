package com.domulink.payment.controller;

import com.domulink.dto.request.NewRentalPaymentRequest;
import com.domulink.dto.request.PayStackWebhookDTO;
import com.domulink.dto.request.RenewRentalPaymentRequest;
import com.domulink.dto.response.PayStackResponse;
import com.domulink.dto.response.TransactionResponse;
import com.domulink.payment.service.PaymentService;
import com.domulink.payment.service.TransactionService;
import com.domulink.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
@Slf4j
public class paymentController {

    private final PaymentService paymentService;
    private final TransactionService transactionService;


    @PostMapping("/new")
    public ResponseEntity<PayStackResponse> payForRent( @Valid @RequestBody NewRentalPaymentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userEmail = customUserDetails.getUsername();

        return ResponseEntity.ok(paymentService.newRentalPayment(userEmail, request));
    }
    @PostMapping("/renew")
    public ResponseEntity<PayStackResponse> renewRent( @Valid @RequestBody RenewRentalPaymentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userEmail = customUserDetails.getUsername();

        return ResponseEntity.ok(paymentService.renewRentalPayment(userEmail, request));
    }

    @GetMapping("/transactions")
    ResponseEntity<List<TransactionResponse>> getUserTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userUuid = customUserDetails.getUuid();
        return ResponseEntity.ok(transactionService.getTransactionsForUser(userUuid));

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
