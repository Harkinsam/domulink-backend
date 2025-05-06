package com.domulink.dto.request;

import com.domulink.enums.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyUpdateRequest {
    private String propertyName;
    
    private PropertyType propertyType;
    
    private FurnishingType furnishingType;
    
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;
    
    private String city;
    
    private String state;
    
    @Min(value = 0, message = "Price cannot be negative")
    private Integer price;
    
    private List<ImageDTO> propertyImages;
    
    private PriceType priceType;

    private String description;
    
    private ElectricityPaymentMode electricityPaymentMode;
    
    private ElectricityFeeInclusion electricityFeeInclusion;
    
    private PropertyStatus propertyStatus;
}