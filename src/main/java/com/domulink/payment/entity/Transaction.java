package com.domulink.payment.entity;

import com.domulink.entity.Rental;
import com.domulink.entity.User;
import com.domulink.enums.PaymentMethod;
import com.domulink.enums.PaymentStatus;
import com.domulink.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String reference;

    private String status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @NotNull
    private BigDecimal amount;
    @NotEmpty
    private String propertyName;

   @NotNull
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "uuid",  nullable = true)
    private User tenant;

    @ManyToOne
    @JoinColumn(name = "landlord_id", referencedColumnName = "uuid",nullable = true)
    private User landlord;



    private LocalDateTime createdAt = LocalDateTime.now();
}


