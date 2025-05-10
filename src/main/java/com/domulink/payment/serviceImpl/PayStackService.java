package com.domulink.payment.serviceImpl;

import com.domulink.dto.request.NewRentalPaymentRequest;
import com.domulink.dto.request.PayStackWebhookDTO;
import com.domulink.dto.request.RenewRentalPaymentRequest;
import com.domulink.dto.response.PayStackResponse;
import com.domulink.entity.Property;
import com.domulink.payment.service.PaymentService;
import com.domulink.repository.PropertyRepository;
import com.domulink.repository.RentalRepository;
import com.domulink.user.service.serviceImp.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayStackService implements PaymentService {

    @Value("${paystack.secret-key}")
    private String payStackSecretKey;

    private final RentalRepository rentalRepository;

    private final RentalService rentalServiceImpl;


    public PayStackResponse newRentalPayment(String email, NewRentalPaymentRequest request) {


        Map<String, Object> metadata = new HashMap<>();
        metadata.put("property_uuid", request.getPropertyUuid());
        metadata.put("property_name", request.getPropertyName());
        metadata.put("price_type", request.getPriceType().toUpperCase());
        metadata.put("landlord_uuid", request.getLandlordUuid());
        metadata.put("tenant_uuid", request.getTenantUuid());
        metadata.put("action", "NEW");

        return sendToPayStack(request.getAmount(),email, metadata);
    }

    public PayStackResponse renewRentalPayment(String email, RenewRentalPaymentRequest request) {
        int amount = rentalRepository.findByUuid(request.getRentalUuid()).orElseThrow().getProperty().getPrice();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("rental_uuid", request.getRentalUuid());
        metadata.put("price_type", request.getPriceType());
        metadata.put("action", "RENEW");

        return sendToPayStack(String.valueOf(amount), email, metadata);
    }

    private PayStackResponse sendToPayStack(String amount, String email, Map<String, Object> metadata) {
        String reference = generateUniqueReference();

        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("email", email);
        paymentDetails.put("amount", (Integer.parseInt(amount) * 100));
        paymentDetails.put("reference", reference);
        paymentDetails.put("callback_url", "http://localhost:8080/api/payments/callback");
        paymentDetails.put("metadata", metadata);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(payStackSecretKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentDetails, headers);
        ResponseEntity<PayStackResponse> response = new RestTemplate().postForEntity(
                "https://api.paystack.co/transaction/initialize",
                request,
                PayStackResponse.class
        );
        log.info("PayStack Response Status: {}", (response.getStatusCode().value()));

        return response.getBody();
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
        log.info("Processing successful charge: {}", data);

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");

        if (metadata == null) {
            log.error("Missing metadata in webhook payload");
            return;
        }

        String action = (String) metadata.get("action");
        if (action == null) {
            log.error("Missing 'action' field in metadata");
            return;
        }

        switch (action) {
            case "NEW":
                log.info("Processing NEW rental...");
                rentalServiceImpl.createRental(data);
                break;
            case "RENEW":
                log.info("Processing RENEW rental...");
                rentalServiceImpl.renewRent(data);
                break;
            default:
                log.warn("Unrecognized action '{}'", action);
                break;
        }
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
