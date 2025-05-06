package com.domulink.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PayStackApiRequestDTO {
    private String email;
    private String payStackAmount;
    private String description;
    private String reference;
    private String  returnUrl;
}
