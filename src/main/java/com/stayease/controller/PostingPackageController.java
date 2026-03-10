package com.stayease.controller;

import com.stayease.dto.request.CreateSubscriptionRequest;
import com.stayease.dto.response.PostingPackageResponse;
import com.stayease.dto.response.PropertyPackageSubscriptionResponse;
import com.stayease.service.PostingPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PostingPackageController {

    private final PostingPackageService postingPackageService;

    @GetMapping
    public ResponseEntity<List<PostingPackageResponse>> getAllActivePackages() {
        return ResponseEntity.ok(postingPackageService.getAllActivePackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostingPackageResponse> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(postingPackageService.getPackageById(id));
    }

    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyPackageSubscriptionResponse> createSubscription(
            @RequestBody CreateSubscriptionRequest request) {
        return ResponseEntity.ok(postingPackageService.createSubscription(request));
    }

    @PutMapping("/confirm-payment/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyPackageSubscriptionResponse> confirmPayment(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(postingPackageService.confirmPayment(transactionId));
    }

    @GetMapping("/property/{propertyId}/subscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PropertyPackageSubscriptionResponse>> getPropertySubscriptions(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(postingPackageService.getPropertySubscriptions(propertyId));
    }
}

