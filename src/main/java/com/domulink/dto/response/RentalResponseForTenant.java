package com.domulink.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RentalResponseForTenant {
    private String rentalUuid;
    private String propertyType;
    private String landlordName;
    private String landlordPhoneNumber;
    private String rentalStatus;
    private int rentAmount;
    private String paymentStatus;

    private LocalDate startDate;
    private LocalDate dueDate;
}
