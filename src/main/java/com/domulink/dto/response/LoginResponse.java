package com.domulink.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponse {
    private String message;
    private String accessToken;
    private String tokenType;
    private String email;

    }
