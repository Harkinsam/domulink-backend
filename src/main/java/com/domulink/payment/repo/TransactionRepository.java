package com.domulink.payment.repo;

import com.domulink.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByTenant_UuidOrLandlord_UuidOrderByPaidAtDesc(String tenantUuid, String landlordUuid);

}
