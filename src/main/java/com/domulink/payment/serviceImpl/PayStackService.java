package com.domulink.payment.serviceImpl;

import com.domulink.dto.request.PayStackApiRequestDTO;
import com.domulink.dto.request.PayStackWebhookDTO;
import com.domulink.dto.response.PayStackResponse;
import com.domulink.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PayStackService implements PaymentService {

    @Value("${paystack.secret-key}")
    private String payStackSecretKey;


    public PayStackResponse initializePayment(String amount, String email, String description) {

        String callbackUrl = "http://localhost:8080/api/payments/callback";
        String reference = generateUniqueReference();

        Map<String, Object> metadata = new HashMap<>(1);// initial capacity to avoid resizing
        metadata.put("description", description);

        Map<String, Object> paymentDetails = new HashMap<>(5);
        paymentDetails.put("email", email);
        paymentDetails.put("amount", Integer.parseInt(amount)* 100); // make sure it's in kobo
        paymentDetails.put("reference", reference);
        paymentDetails.put("callback_url", callbackUrl);
        paymentDetails.put("metadata", metadata);

        log.info("Payment details{}", paymentDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(payStackSecretKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentDetails, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<PayStackResponse> response = restTemplate.postForEntity(
                    "https://api.paystack.co/transaction/initialize",
                    request,
                    PayStackResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("PayStack API error: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("PayStack API error: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Exception during payment initialization: {}", ex.getMessage());
            throw new RuntimeException("Payment initialization failed", ex);
        }
    }



    private String generateUniqueReference() {
        String prefix = "TXN_";
        String timestampPart = String.valueOf(System.currentTimeMillis());
        String uuidPart = UUID.randomUUID().toString().split("-")[0]; // Shortened UUID (first part)

        return prefix + timestampPart + "_" + uuidPart;
    }


    public void handleWebhook(PayStackWebhookDTO webhookPayload) {
        log.info("Received Paystack webhook: {}", webhookPayload.getEvent());

        String event = webhookPayload.getEvent();

        Map<String, Object> data = webhookPayload.getData();


        switch (event) {
            case "charge.success":
                handleSuccessfulCharge(data);
                break;
            case "transfer.success":
                handleSuccessfulTransfer(data);
                break;
            case "transfer.failed":
                handleFailedTransfer(data);
                break;
            default:
                log.info("Unhandled event type: {}", event);
                break;
        }
    }

    private void handleSuccessfulCharge(Map<String, Object> data) {
        // Extract relevant information from the data
        log.info("Processing successful charge: {}", data);

        // Implement your business logic here
        // For example, update the payment status in your database
    }

    private void handleSuccessfulTransfer(Map<String, Object> data) {
        // Extract relevant information from the data
        log.info("Processing successful transfer: {}", data);

        // Implement your business logic here
    }

    private void handleFailedTransfer(Map<String, Object> data) {
        // Extract relevant information from the data
        log.error("Processing failed transfer: {}", data);

        // Implement your business logic here
    }
}
