package com.domulink.entity;

import com.domulink.entity.Image;
import com.domulink.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "properties", indexes = {
        @Index(name = "idx_uuid", columnList = "uuid", unique = true)
})
public class Property {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "uuid")
    private User landlord;

    @NotBlank(message = "Property name is required")
    @Column(name = "property_name", nullable = false)
    private String propertyName;

    @Column(name = "property_type")
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Column(name = "furnishing_type")
    @Enumerated(EnumType.STRING)
    private FurnishingType furnishingType;


    @Enumerated(EnumType.STRING)
    @Column(name = "property_status")
    private PropertyStatus propertyStatus = PropertyStatus.AVAILABLE;


    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    @Column(name = "state", nullable = false)
    private String state;

    @Min(value = 0, message = "Price cannot be negative")
    @Column(nullable = false)
    private Integer price;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "property_uuid", referencedColumnName = "uuid")
    private List<Image> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceType priceType;

    @Column(name = "electricity_payment_mode")
    @Enumerated(EnumType.STRING)
    private ElectricityPaymentMode electricityPaymentMode;

    @Column(name = "electricity_fee_inclusion")
    @Enumerated(EnumType.STRING)
    private ElectricityFeeInclusion electricityFeeInclusion;
    ;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String documentUrl;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDate updatedAt;

}
