package com.domulink.repository;

import com.domulink.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Modifying
    @Query("DELETE FROM Property p WHERE p.uuid = :uuid")
    void deleteByUuid(@Param("uuid") String uuid);

    Optional<Property> findByUuid(String uuid);

    Page<Property> findByIsApproved(boolean approved, Pageable pageable);

    Page<Property> findByLandlord_Uuid(String landlordUuid, Pageable pageable);
}
