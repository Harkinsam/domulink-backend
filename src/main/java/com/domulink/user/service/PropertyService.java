package com.domulink.user.service;

import com.domulink.dto.request.PropertyCreateRequest;
import com.domulink.dto.request.PropertyUpdateRequest;
import com.domulink.dto.response.PropertyFetchResponse;
import com.domulink.dto.response.PropertyPageResponse;
import com.domulink.dto.response.PropertyResponse;
import org.springframework.data.domain.Pageable;

public interface PropertyService {
    PropertyResponse createProperty(String userUuid, PropertyCreateRequest propertyCreateRequest);

    PropertyResponse deleteProperty(String uuid, String userUuid);

    PropertyFetchResponse getSingleProperty(String uuid);

    PropertyPageResponse getAllProperties(Pageable pageable);

    PropertyPageResponse getPropertiesByApproved(boolean isApproved, Pageable pageable);

    PropertyPageResponse getPropertiesByLandlord(String landlordUuid, Pageable pageable);

    PropertyResponse approveProperty(String propertyUuid);

    PropertyResponse updateProperty(String propertyUuid, String userUuid, PropertyUpdateRequest updateRequest);
}
