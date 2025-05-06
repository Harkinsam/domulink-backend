package com.domulink.dto.response;


import com.domulink.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyFetchResponse {

    private String uuid;
    private String propertyName;
    private PropertyType propertyType;
    private FurnishingType furnishingType;
    private PropertyStatus propertyStatus;
    private String address;
    private String city;
    private String state;
    private Integer price;
    private PriceType priceType;
    private ElectricityPaymentMode electricityPaymentMode;
    private ElectricityFeeInclusion electricityFeeInclusion;
    private String description;
    private String documentUrl;
    private boolean isApproved;
    private List<ImageDTO> images;
    private LandlordDTO landlord;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDTO {
        private String url;
        private Boolean isPrimary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LandlordDTO {
        private String uuid;
        private String email;
    }
}


