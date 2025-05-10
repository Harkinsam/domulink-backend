package com.domulink.repository;

import com.domulink.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByUuid(String uuid);

    Optional<Rental> findByTenant_Uuid(String tenantUuid);

    List<Rental> findByLandlord_Uuid(String landlordUuid);




}
