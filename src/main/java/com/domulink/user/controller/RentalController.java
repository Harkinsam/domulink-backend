package com.domulink.user.controller;

import com.domulink.dto.response.RentalResponseForTenant;
import com.domulink.security.CustomUserDetails;
import com.domulink.user.service.serviceImp.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rental")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;


    @GetMapping("/tenant")
    public ResponseEntity<RentalResponseForTenant> getTenantRental() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userUuid = customUserDetails.getUuid();
        return ResponseEntity.ok(rentalService.getRental(userUuid));

    }

    @PatchMapping("/{uuid}/disable")// landlord role
    public ResponseEntity<String> disableRental(@PathVariable String uuid) {
        String result = rentalService.disableRental(uuid);
        return ResponseEntity.ok(result);
    }


}
