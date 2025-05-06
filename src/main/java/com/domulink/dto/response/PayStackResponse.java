package com.domulink.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString

public class PayStackResponse {
    private boolean status;
    private String message;
    private Data data;

    // Inner class for the 'data' part of the response
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
//    @ToString
    public static class Data {
        private String authorization_url;
        @JsonIgnore
        private String access_code;
        private String reference;
    }
}