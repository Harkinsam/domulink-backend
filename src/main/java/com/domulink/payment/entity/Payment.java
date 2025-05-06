package com.domulink.payment.entity;

import com.domulink.entity.Rental;
import com.domulink.enums.PaymentMethod;
import com.domulink.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull
    @Column(nullable = false, unique = true)
    private String transactionReference;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

