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
public class PayStackPaymentDTO {
    @NotEmpty(message = "Amount is required")
    private String amount;

    @NotEmpty(message = "description cannot be empty")
    private String description;


}

