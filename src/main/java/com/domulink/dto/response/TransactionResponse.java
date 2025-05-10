package com.domulink.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransactionResponse(
        String reference,
        BigDecimal amount,
        String type,
        String status,
        LocalDateTime paidAt,
        String propertyName
) {}
