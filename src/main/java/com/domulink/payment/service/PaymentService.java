package com.domulink.payment.service;

import com.domulink.dto.request.PayStackWebhookDTO;
import com.domulink.dto.response.PayStackResponse;

public interface PaymentService {

    PayStackResponse initializePayment(String amount, String email, String description);

    void handleWebhook(PayStackWebhookDTO webhookPayload);
}
