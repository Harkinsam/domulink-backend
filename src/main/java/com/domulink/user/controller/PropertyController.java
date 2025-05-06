package com.domulink.user.controller;

import com.domulink.dto.response.PropertyFetchResponse;
import com.domulink.dto.response.PropertyPageResponse;
import com.domulink.security.CustomUserDetails;
import com.domulink.user.service.PropertyService;
import com.domulink.dto.request.PropertyCreateRequest;
import com.domulink.dto.request.PropertyUpdateRequest;
import com.domulink.dto.response.PropertyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @PostMapping("/new")
    public ResponseEntity<PropertyResponse> createProperty(@ModelAttribute @Valid PropertyCreateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userUuid = customUserDetails.getUuid();
        return ResponseEntity.ok(propertyService.createProperty(userUuid,request));
    }

    @GetMapping("/{propertyUuid}")
    public ResponseEntity<PropertyFetchResponse> getProperty(@PathVariable String propertyUuid) {
        return ResponseEntity.ok(propertyService.getSingleProperty(propertyUuid));
    }

    @DeleteMapping("/{propertyUuid}")
    public ResponseEntity<PropertyResponse> deleteProperty(@PathVariable String propertyUuid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userUuid = customUserDetails.getUuid();

        return ResponseEntity.ok(propertyService.deleteProperty(propertyUuid, userUuid));
    }

    @GetMapping("/all")
    public ResponseEntity<PropertyPageResponse> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(propertyService.getAllProperties(pageable));
    }

    @GetMapping("/approved")
    public ResponseEntity<PropertyPageResponse> getPropertiesByApproved(
            @RequestParam boolean isApproved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(propertyService.getPropertiesByApproved(isApproved, pageable));
    }

    @GetMapping("/landlord/{landlordUuid}")
    public ResponseEntity<PropertyPageResponse> getPropertiesByLandlord(
            @PathVariable String landlordUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(propertyService.getPropertiesByLandlord(landlordUuid, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<PropertyPageResponse> getMyProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userUuid = customUserDetails.getUuid();

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(propertyService.getPropertiesByLandlord(userUuid, pageable));
    }

    @PatchMapping("/{propertyUuid}/approve")
    public ResponseEntity<PropertyResponse> approveProperty(@PathVariable String propertyUuid) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!authentication.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
//            return ResponseEntity.status(403).body(new PropertyResponse(propertyUuid, "Only administrators can approve properties"));
//        }
        return ResponseEntity.ok(propertyService.approveProperty(propertyUuid));
    }

    @PatchMapping("/{propertyUuid}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable String propertyUuid,
            @ModelAttribute PropertyUpdateRequest updateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userUuid = customUserDetails.getUuid();

        return ResponseEntity.ok(propertyService.updateProperty(propertyUuid, userUuid, updateRequest));
    }
}
