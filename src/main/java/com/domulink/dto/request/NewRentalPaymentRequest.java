package com.domulink.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewRentalPaymentRequest {
    @NotEmpty(message = "Amount is required")
    private String amount;

//    @NotEmpty(message = "description cannot be empty")
//    private String description;

    @NotEmpty(message = "Property UUID cannot be empty")
    private String propertyUuid;

    @NotEmpty(message = "Property name cannot be empty")
    private String propertyName;

    @NotEmpty(message = "Price type cannot be empty")
    private String priceType;

    @NotEmpty(message = "landlord uuid cannot be empty")
    private String landlordUuid;

    @NotEmpty(message = "tenant cannot be empty")
    private String tenantUuid;

}

