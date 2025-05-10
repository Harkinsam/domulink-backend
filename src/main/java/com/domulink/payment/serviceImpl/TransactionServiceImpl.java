package com.domulink.payment.serviceImpl;


import com.domulink.dto.response.TransactionResponse;
import com.domulink.entity.User;
import com.domulink.enums.TransactionType;
import com.domulink.payment.entity.Transaction;
import com.domulink.payment.repo.TransactionRepository;
import com.domulink.payment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;


    public void recordRentalPayment(Map<String, Object> data, User tenant, User landlord, String propertyName,String transactionType) {

        OffsetDateTime paidAt = OffsetDateTime.parse(data.get("paid_at").toString());

        Transaction txn = new Transaction();
        txn.setUuid(UUID.randomUUID().toString());
        txn.setReference((String) data.get("reference"));
        txn.setStatus((String) data.get("status"));
        txn.setType(TransactionType.valueOf(transactionType));
        txn.setAmount(new BigDecimal(data.get("amount").toString())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        );

        txn.setPaidAt(paidAt.toLocalDateTime());
        txn.setPropertyName(propertyName);
        txn.setTenant(tenant);
        txn.setLandlord(landlord);


        transactionRepository.save(txn);
        log.info("Transaction recorded for rental payment. Reference: {}", txn.getReference());
    }

    public List<TransactionResponse> getTransactionsForUser(String userUuid) {
        List<Transaction>  existingTransaction = transactionRepository.findAllByTenant_UuidOrLandlord_UuidOrderByPaidAtDesc(userUuid, userUuid);

        return existingTransaction.stream()
               .map(txn -> new TransactionResponse(
                       txn.getReference(),
                       txn.getAmount(),
                       txn.getType().name(),
                       txn.getStatus(),
                       txn.getPaidAt(),
                       txn.getPropertyName()

               ))
               .toList();

    }


    }


