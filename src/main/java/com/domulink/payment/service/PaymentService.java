package com.domulink.payment.service;

import com.domulink.dto.request.NewRentalPaymentRequest;
import com.domulink.dto.request.PayStackWebhookDTO;
import com.domulink.dto.request.RenewRentalPaymentRequest;
import com.domulink.dto.response.PayStackResponse;

public interface PaymentService {

    PayStackResponse renewRentalPayment(String userEmail,RenewRentalPaymentRequest request);
    PayStackResponse newRentalPayment(String userEmail, NewRentalPaymentRequest request);

    void handleWebhook(PayStackWebhookDTO webhookPayload);
}