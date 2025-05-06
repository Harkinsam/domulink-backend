package com.domulink.util;


import com.domulink.dto.response.PropertyFetchResponse;
import com.domulink.entity.Property;


import java.util.List;
import java.util.stream.Collectors;


    public class PropertyMapper {

        public static PropertyFetchResponse toPropertyFetchResponse(Property property) {

            List<PropertyFetchResponse.ImageDTO> imageDTOs = property.getImages().stream()
                    .map(image -> new PropertyFetchResponse.ImageDTO(image.getUrl(), image.isPrimary()))
                    .collect(Collectors.toList());

            PropertyFetchResponse.LandlordDTO landlordDTO = new PropertyFetchResponse.LandlordDTO(
                    property.getLandlord().getUuid(),
                    property.getLandlord().getEmail()
            );

            return PropertyFetchResponse.builder()
                    .uuid(property.getUuid())
                    .propertyName(property.getPropertyName())
                    .propertyType(property.getPropertyType())
                    .furnishingType(property.getFurnishingType())
                    .propertyStatus(property.getPropertyStatus())
                    .address(property.getAddress())
                    .city(property.getCity())
                    .state(property.getState())
                    .price(property.getPrice())
                    .priceType(property.getPriceType())
                    .electricityPaymentMode(property.getElectricityPaymentMode())
                    .electricityFeeInclusion(property.getElectricityFeeInclusion())
                    .description(property.getDescription())
                    .documentUrl(property.getDocumentUrl())
                    .isApproved(property.isApproved())
                    .images(imageDTOs)
                    .landlord(landlordDTO)
                    .build();
        }
    }

