package com.domulink.user.service.serviceImp;

import com.domulink.dto.response.RentalResponseForTenant;
import com.domulink.entity.Property;
import com.domulink.entity.Rental;
import com.domulink.entity.User;
import com.domulink.enums.PropertyStatus;
import com.domulink.enums.RentalPaymentStatus;
import com.domulink.enums.RentalStatus;
import com.domulink.notification.email.EmailService;
import com.domulink.payment.service.TransactionService;
import com.domulink.repository.PropertyRepository;
import com.domulink.repository.RentalRepository;
import com.domulink.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final TransactionService transactionService;
    private final EmailService emailService;



    @Transactional
    public void createRental(Map<String, Object> data) {
        // Extract metadata from webhook payload
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
        log.info("Metadata in CreateRental(): {}", metadata);
        if (metadata == null ||
                !metadata.containsKey("property_uuid") ||
                !metadata.containsKey("landlord_uuid") ||
                !metadata.containsKey("tenant_uuid") ||
                !metadata.containsKey("price_type") ||
                !data.containsKey("paid_at")) {
            log.error("Invalid or missing metadata in webhook payload");
            return;
        }

        OffsetDateTime presentDate = OffsetDateTime.parse(data.get("paid_at").toString());


        String propertyUuid = (String) metadata.get("property_uuid");
        String landlordUuid = (String) metadata.get("landlord_uuid");
        String tenantUuid = (String) metadata.get("tenant_uuid");
        String priceType = (String) metadata.get("price_type");

        // Fetch both landlord and tenant users in a single query to avoid multiple DB calls
        List<User> users = userRepository.findAllByUuidIn(List.of(landlordUuid, tenantUuid));
        if (users.size() != 2) {
            log.error("Landlord or Tenant not found for UUIDs: {}, {}", landlordUuid, tenantUuid);
            throw new EntityNotFoundException("Landlord or Tenant not found");
        }

        User landlord = users.get(0).getUuid().equals(landlordUuid) ? users.get(0) : users.get(1);
        User tenant = users.get(0).getUuid().equals(tenantUuid) ? users.get(0) : users.get(1);

        Property property = propertyRepository.findByUuid(propertyUuid)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        if (PropertyStatus.UNAVAILABLE.equals(property.getPropertyStatus())) {
            log.error("Property with UUID: {} is already rented", propertyUuid);
            return;
        }

        Rental rental = new Rental();
        rental.setUuid(UUID.randomUUID().toString());
        rental.setLandlord(landlord);
        rental.setTenant(tenant);
        rental.setProperty(property);
        rental.setRentalStatus(RentalStatus.ACTIVE);
        rental.setRenewalAllowed(true);

        log.info("Creating rental for property UUID: {}", propertyUuid);

        setRentalDateAndSaveToDb(presentDate.toLocalDate(), priceType, rental);
        log.info("Rental created successfully for property UUID: {}", propertyUuid);

        property.setPropertyStatus(PropertyStatus.UNAVAILABLE);
        propertyRepository.save(property);
        log.info("Property status updated to UNAVAILABLE for property UUID: {}", propertyUuid);

        log.info("Saving transaction for property UUID: {}", propertyUuid);

        transactionService.recordRentalPayment(data,tenant,landlord,property.getPropertyName(),"RENT_PAYMENT");

    }

    public void renewRent (Map<String,Object> data){
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
        String priceType = (String) metadata.get("price_type");
        String rentalUuid = (String) metadata.get("rental_uuid");

        Rental rental = rentalRepository.findByUuid(rentalUuid)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found"));

        setRentalDateAndSaveToDb(rental.getEndDate(), priceType, rental);
        log.info("Rental renewed successfully for property UUID: {}", rental.getProperty().getUuid());

        transactionService.recordRentalPayment(data,rental.getTenant(),rental.getLandlord(),rental.getProperty().getPropertyName(),"RENT_RENEWAL");



    }


    public RentalResponseForTenant getRental(String uuid){
        Rental rental = rentalRepository.findByTenant_Uuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Rental not found"));

        Property property = rental.getProperty();
        User landLord = rental.getLandlord();
        RentalResponseForTenant response = new RentalResponseForTenant();
        response.setRentalUuid(rental.getUuid());
        response.setRentalStatus(rental.getRentalStatus().toString());
        response.setLandlordName(landLord.getFirstName());
        response.setPropertyType((property.getPropertyType().toString()));
        response.setDueDate(rental.getEndDate());
        response.setStartDate(rental.getStartDate());
        response.setRentAmount(property.getPrice());

        response.setPaymentStatus(rental.getPaymentStatus().toString());

        return response;

    }

    public String disableRental(String uuid){
        Rental rental = rentalRepository.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException("Rental not found"));
        rental.setRenewalAllowed(false);
        rentalRepository.save(rental);
        rentalRepository.save(rental);

//        emailService.sendRenewalDisabledEmail(
//                rental.getTenant().getEmail(),
//                rental.getTenant().getFirstName(),
//                rental.getProperty().getPropertyName()
//        );

        return "Rental renewal has been disabled. A notification email has been sent to the tenant.";


    }

    private void setRentalDateAndSaveToDb(LocalDate date, String priceType, Rental rental) {


        rental.setStartDate(date);

        switch (priceType.toUpperCase()) {
            case "MONTHLY":
                rental.setEndDate(date.plusMonths(1));
                break;
            case "YEARLY":
                rental.setEndDate(date.plusYears(1));
                break;
            default:
                log.error("Unsupported price type: {}", priceType);
                throw new IllegalArgumentException("Unsupported price type: " + priceType);
        }

        rental.setPaymentStatus(RentalPaymentStatus.PAID);
        rentalRepository.save(rental);
    }


}
