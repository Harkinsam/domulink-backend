package com.domulink.entity;

import com.domulink.enums.RentalPaymentStatus;
import com.domulink.enums.RentalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String uuid;

    @OneToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "uuid",columnDefinition = "VARCHAR(255)", nullable = false,unique = true)
    private User tenant;

    @OneToOne
    @JoinColumn(name = "property_id",referencedColumnName = "uuid",columnDefinition = "VARCHAR(255)", nullable = false,unique = true) //columnDefinition = "VARCHAR(255)",
    private Property property;

    @ManyToOne
    @JoinColumn(name = "landlord_id", referencedColumnName = "uuid", columnDefinition = "VARCHAR(255)", nullable = false)
    private User landlord;

    @Column(name = "renewal_allowed", nullable = false)
    private boolean renewalAllowed;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private RentalStatus rentalStatus;

    @Enumerated(EnumType.STRING)
    private RentalPaymentStatus paymentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDate updatedAt;

}

