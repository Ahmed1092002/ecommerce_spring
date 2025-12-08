package com.example.test_ecommerce.ecommerce.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerAnalyticsResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerStatsResponse;
import com.example.test_ecommerce.ecommerce.services.SellerAnalyticsService;
import com.example.test_ecommerce.ecommerce.services.SellerProfileService;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/profile")
@PreAuthorize("hasRole('SELLER')")
@Tag(name = "Seller Profile", description = "Seller profile and statistics management")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;
    private final SellerAnalyticsService sellerAnalyticsService;

    public SellerProfileController(SellerProfileService sellerProfileService,
            SellerAnalyticsService sellerAnalyticsService) {
        this.sellerProfileService = sellerProfileService;
        this.sellerAnalyticsService = sellerAnalyticsService;
    }

    @GetMapping
    @Operation(summary = "Get seller profile", description = "Retrieve the authenticated seller's profile information")
    public ResponseEntity<SellerProfileResponse> getProfile() {
        return ResponseEntity.ok(sellerProfileService.getSellerProfile());
    }

    @PutMapping
    @Operation(summary = "Update seller profile", description = "Update the authenticated seller's profile information")
    public ResponseEntity<SellerProfileResponse> updateProfile(@Valid @RequestBody SellerProfileDto dto) {
        return ResponseEntity.ok(sellerProfileService.updateSellerProfile(dto));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get seller statistics", description = "Retrieve basic statistics for the authenticated seller")
    public ResponseEntity<SellerStatsResponse> getStats() {
        return ResponseEntity.ok(sellerProfileService.getSellerStats());
    }

    @PostMapping
    @Operation(summary = "Create seller profile", description = "Create a new seller profile for the authenticated user")
    public ResponseEntity<Map<String, Object>> createProfile(@Valid @RequestBody SellerProfileDto dto) {
        return ResponseEntity.ok(sellerProfileService.createSellerProfile(dto));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get comprehensive seller analytics", description = "Retrieve detailed analytics including revenue, orders, products, and time-based analysis")
    public ResponseEntity<SellerAnalyticsResponse> getAnalytics(
            @Parameter(description = "Start date for analytics (format: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,

            @Parameter(description = "End date for analytics (format: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Convert LocalDate to LocalDateTime
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        return ResponseEntity.ok(sellerAnalyticsService.getSellerAnalytics(startDateTime, endDateTime));
    }
}
