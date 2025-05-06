package com.domulink.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyPageResponse {
    private List<PropertyFetchResponse> properties;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}