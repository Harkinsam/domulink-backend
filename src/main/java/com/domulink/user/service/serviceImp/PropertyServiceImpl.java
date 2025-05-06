package com.domulink.user.service.serviceImp;

import com.domulink.dto.request.ImageDTO;
import com.domulink.dto.request.PropertyCreateRequest;
import com.domulink.dto.request.PropertyUpdateRequest;
import com.domulink.dto.response.PropertyFetchResponse;
import com.domulink.dto.response.PropertyPageResponse;
import com.domulink.dto.response.PropertyResponse;
import com.domulink.entity.Image;
import com.domulink.entity.Property;
import com.domulink.entity.User;
import com.domulink.exception.PropertyNotFoundException;
import com.domulink.exception.UserNotFoundException;
import com.domulink.repository.PropertyRepository;
import com.domulink.repository.UserRepository;
import com.domulink.user.service.PropertyService;
import com.domulink.user.service.StorageService;
import com.domulink.util.PropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final StorageService storageService;
    private final ThreadPoolTaskExecutor uploadExecutor;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public PropertyResponse createProperty(String userUuid,PropertyCreateRequest request) {
        log.info("Creating property with name {}", request.getPropertyName());

        if (request.getPropertyImages() == null || request.getPropertyImages().isEmpty()) {
            throw new IllegalArgumentException("At least one image is required.");
        }

        if (request.getDocumentImage() == null || request.getDocumentImage().isEmpty()) {
            throw new IllegalArgumentException("Document image is required.");
        }

        boolean primaryImageFound = request.getPropertyImages().stream()
                .anyMatch(ImageDTO::isPrimary);

        if (!primaryImageFound) {
            log.error("No primary image found");
            throw new IllegalArgumentException("At least one image must be marked as primary.");
        }
        User landlord = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("User not found with UUID: " + userUuid));

        Property property = new Property();
        property.setUuid(UUID.randomUUID().toString());
        property.setLandlord(landlord);
        property.setPropertyName(request.getPropertyName());
        property.setPropertyType(request.getPropertyType());
        property.setFurnishingType(request.getFurnishingType());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setPrice(request.getPrice());
        property.setPriceType(request.getPriceType());
        property.setDescription(request.getDescription());
        property.setElectricityPaymentMode(request.getElectricityPaymentMode());
        property.setElectricityFeeInclusion(request.getElectricityFeeInclusion());

        log.info("Submitting document upload task to executor");
        CompletableFuture<String> documentImage = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            log.info("Uploading document image on thread: {}", threadName);
            return storageService.uploadPropertyDocumentImage(request.getDocumentImage());
        }, uploadExecutor);

        log.info("Submitting property image uploads to executor");
        List<Image> images = updatePropertyImages(request.getPropertyImages(),property);


        String documentUrl = documentImage.join();
        property.setDocumentUrl(documentUrl);
        log.info("Document image uploaded successfully: {}", documentUrl);

        property.getImages().addAll(images);


        log.info("Saving property to database...");
        Property savedProperty = propertyRepository.save(property);

        log.info("Property created successfully with ID {}", savedProperty.getUuid());
        return new PropertyResponse(savedProperty.getUuid(), "Property created successfully");
    }


    public PropertyFetchResponse getSingleProperty(String uuid) {
        log.info("Fetching property with UUID: {}", uuid);
        Optional<Property> property = propertyRepository.findByUuid(uuid);
        if (property.isPresent()) {
            return PropertyMapper.toPropertyFetchResponse(property.get());
        } else {
            throw new PropertyNotFoundException("Property not found with UUID: " + uuid);
        }
    }


    public PropertyResponse deleteProperty(String uuid, String userUuid) {
        log.info("Attempting to delete property with UUID: {} by user: {}", uuid, userUuid);

        Property property = propertyRepository.findByUuid(uuid)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with UUID: " + uuid));

        // Optionally check ownership
        // if (!property.getLandlord().getUuid().equals(userUuid)) {
        //     throw new IllegalArgumentException("You are not authorized to delete this property");
        // }

        List<Image> images = property.getImages();

        // Delete property images asynchronously
        List<CompletableFuture<Void>> deleteFutures = new ArrayList<>(images.stream()
                .map(image -> CompletableFuture.runAsync(() -> {
                    String publicId = extractPropertyIdFromUrl(image.getUrl());
                    storageService.deleteImage(publicId);
                }, uploadExecutor))
                .toList());

        if (property.getDocumentUrl() != null) {
            CompletableFuture<Void> documentDelete = CompletableFuture.runAsync(() -> {
                String docPublicId = extractPropertyDocIdFromUrl(property.getDocumentUrl());
                storageService.deleteImage(docPublicId);
            }, uploadExecutor);

            deleteFutures.add(documentDelete);

        }

        CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0])).join();

        propertyRepository.delete(property);

        log.info("Property and associated media deleted successfully.");
        return new PropertyResponse(uuid, "Property deleted successfully");
    }




    public PropertyPageResponse getAllProperties(Pageable pageable) {
        log.info("Fetching all properties with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Property> propertyPage = propertyRepository.findAll(pageable);

        List<PropertyFetchResponse> propertyResponses = propertyPage.getContent().stream()
                .map(PropertyMapper::toPropertyFetchResponse)
                .toList();

        return PropertyPageResponse.builder()
                .properties(propertyResponses)
                .page(propertyPage.getNumber())
                .size(propertyPage.getSize())
                .totalElements(propertyPage.getTotalElements())
                .totalPages(propertyPage.getTotalPages())
                .last(propertyPage.isLast())
                .build();
    }

    @Override
    public PropertyPageResponse getPropertiesByApproved(boolean approved, Pageable pageable) {
        log.info("Fetching properties by approved status: {}, page={}, size={}", 
                approved, pageable.getPageNumber(), pageable.getPageSize());

        Page<Property> propertyPage = propertyRepository.findByIsApproved(approved, pageable);

        List<PropertyFetchResponse> propertyResponses = propertyPage.getContent().stream()
                .map(PropertyMapper::toPropertyFetchResponse)
                .toList();

        return PropertyPageResponse.builder()
                .properties(propertyResponses)
                .page(propertyPage.getNumber())
                .size(propertyPage.getSize())
                .totalElements(propertyPage.getTotalElements())
                .totalPages(propertyPage.getTotalPages())
                .last(propertyPage.isLast())
                .build();
    }

    public PropertyPageResponse getPropertiesByLandlord(String landlordUuid, Pageable pageable) {
        log.info("Fetching properties by landlord UUID: {}, page={}, size={}", 
                landlordUuid, pageable.getPageNumber(), pageable.getPageSize());

        Page<Property> propertyPage = propertyRepository.findByLandlord_Uuid(landlordUuid, pageable);
        log.info(String.valueOf(propertyPage.getNumber()));

        List<PropertyFetchResponse> propertyResponses = propertyPage.getContent().stream()
                .map(PropertyMapper::toPropertyFetchResponse)
                .toList();

        return PropertyPageResponse.builder()
                .properties(propertyResponses)
                .page(propertyPage.getNumber())
                .size(propertyPage.getSize())
                .totalElements(propertyPage.getTotalElements())
                .totalPages(propertyPage.getTotalPages())
                .last(propertyPage.isLast())
                .build();
    }


    public PropertyResponse approveProperty(String propertyUuid) {
        log.info("Approving property with UUID: {}", propertyUuid);

        Property property = propertyRepository.findByUuid(propertyUuid)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with UUID: " + propertyUuid));

        property.setApproved(true);
        propertyRepository.save(property);

        log.info("Property with UUID: {} has been approved", propertyUuid);
        return new PropertyResponse(propertyUuid, "Property approved successfully");
    }

    public PropertyResponse updateProperty(String propertyUuid, String userUuid, PropertyUpdateRequest updateRequest) {
        log.info("Updating property with UUID: {} by user: {}", propertyUuid, userUuid);

        Property property = propertyRepository.findByUuid(propertyUuid)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with UUID: " + propertyUuid));

        modelMapper.map(updateRequest, property);

        if (updateRequest.getPropertyImages() != null && !updateRequest.getPropertyImages().isEmpty()) {
            boolean primaryImageFound = updateRequest.getPropertyImages().stream()
                    .anyMatch(ImageDTO::isPrimary);

            if (!primaryImageFound) {
                log.error("No primary image found in update request");
                throw new IllegalArgumentException("At least one image must be marked as primary.");
            }

            property.getImages().clear();

            List<Image> images = updatePropertyImages(updateRequest.getPropertyImages(),property);


            property.getImages().addAll(images);
        }

        Property updatedProperty = propertyRepository.save(property);

        log.info("Property updated successfully with ID {}", updatedProperty.getUuid());
        return new PropertyResponse(updatedProperty.getUuid(), "Property updated successfully");
    }

    private List<Image> updatePropertyImages(List<ImageDTO> propertyImages, Property existingProperty) {
        List<CompletableFuture<String>> uploadedImages = propertyImages.stream()
                .map(imageDTO -> CompletableFuture.supplyAsync(() -> {
                    MultipartFile file = imageDTO.getFile();
                    String existingUrl = getExistingImageUrl(existingProperty, file);

                    if (existingUrl != null) {
                        // If the image already exists, skip upload and return the existing URL
                        return existingUrl;
                    }

                    String threadName = Thread.currentThread().getName();
                    log.info("Uploading image '{}' on thread: {}", file.getOriginalFilename(), threadName);
                    return storageService.uploadPropertyImage(file);  // Proceed with upload if not a duplicate
                }, uploadExecutor))
                .toList();

        log.info("Waiting for image uploads to complete...");
        CompletableFuture.allOf(uploadedImages.toArray(new CompletableFuture[0])).join();

        List<Image> newImages = new ArrayList<>();
        for (int i = 0; i < propertyImages.size(); i++) {
            String url = uploadedImages.get(i).join();
            ImageDTO imageDTO = propertyImages.get(i);
            Image image = new Image();
            image.setUrl(url);
            image.setPrimary(imageDTO.isPrimary());
            newImages.add(image);
        }
        return newImages;
    }

    private String getExistingImageUrl(Property property, MultipartFile file) {

        for (Image image : property.getImages()) {
            if (image.getUrl().contains(Objects.requireNonNull(file.getOriginalFilename()))) {
                log.info("Image '{}' already exists, skipping upload.", file.getOriginalFilename());
                return image.getUrl(); // Return existing URL if duplicate found
            }
        }
        return null;
    }

    private String extractPropertyIdFromUrl(String url) {
        // Example for URL: https://res.cloudinary.com/your-cloud/image/upload/v1234567890/property-images/sample.jpg
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1]; // sample.jpg
        return "property-images/" + publicIdWithExtension.replace(".jpg", "");
    }

    private String extractPropertyDocIdFromUrl(String url) {
        // Example for URL: https://res.cloudinary.com/your-cloud/image/upload/v1234567890/property-images/sample.jpg
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1]; // sample.jpg
        return "property-document-images/" + publicIdWithExtension.replace(".jpg", "");
    }




}
