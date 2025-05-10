package com.domulink.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RenewRentalPaymentRequest {

    @NotEmpty(message = "rental uuid is required")
    private String rentalUuid;
    @NotEmpty(message = "Price type is required")
    private String priceType;
}
