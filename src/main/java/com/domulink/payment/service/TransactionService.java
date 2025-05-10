package com.domulink.payment.service;

import com.domulink.dto.response.TransactionResponse;
import com.domulink.entity.User;
import com.domulink.payment.entity.Transaction;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<TransactionResponse> getTransactionsForUser(String userUuid);

    void recordRentalPayment(Map<String, Object> data, User tenant, User landlord, String propertyName,String transactionType);
}

