package com.domulink.entity;

import com.domulink.enums.Role;
import com.domulink.enums.EmploymentStatus;
import com.domulink.enums.KycStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
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
@Table(name = "user_accounts", indexes = {
		@Index(name = "idx_email", columnList = "email", unique = true),
		@Index(name = "idx_uuid", columnList = "uuid", unique = true)
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;

	@NotBlank(message = "Phone number is required")
	@Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
	@Column(name = "phone_number", unique = true, nullable = false, length = 15)
	private String phoneNumber;

	private boolean phoneVerified = false;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Column(name = "email", unique = true, nullable = false, length = 100)
	private String email;

	@NotBlank(message = "First name is required")
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "govt_id_url")
	private String govtIdUrl;

//	@Size(max = 255, message = "Property proof URL must be at most 255 characters")
//	@Column(name = "property_proof_url", length = 255)
//	private String propertyProofUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "kyc_status")
	private KycStatus kycStatus;

	@Column(name = "bank_account_details")
	private String bankAccountNumber;

	@Column(name = "account_name")
	private String AccountName;

	@Enumerated(EnumType.STRING)
	@Column(name = "employment_status")
	private EmploymentStatus employmentStatus;


//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//	@JoinColumn(name = "landlord_id") // Foreign key in Property table
//	private List<Property> properties = new ArrayList<>();
//
//
//	@Column(name = "verified", nullable = false)
//	private boolean verified;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;


	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDate createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDate updatedAt;
}



