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
public class PropertyCreateRequest {
    @NotBlank(message = "Property name is required")
    private String propertyName;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotNull(message = "Furnishing type is required")
    private FurnishingType furnishingType;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Min(value = 0, message = "Price cannot be negative")
    @NotNull(message = "Price is required")
    private Integer price;

    private List<ImageDTO> propertyImages;

    private MultipartFile documentImage;

    @NotNull(message = "Price type is required")
    private PriceType priceType;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @NotNull(message = "Electricity payment mode is required")
    private ElectricityPaymentMode electricityPaymentMode;

    @NotNull(message = "Electricity fee inclusion is required")
    private ElectricityFeeInclusion electricityFeeInclusion;
}
