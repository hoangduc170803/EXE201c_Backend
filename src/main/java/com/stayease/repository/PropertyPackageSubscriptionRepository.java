package com.stayease.repository;

import com.stayease.model.PropertyPackageSubscription;
import com.stayease.enums.PackageSubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyPackageSubscriptionRepository extends JpaRepository<PropertyPackageSubscription, Long> {

    List<PropertyPackageSubscription> findByPropertyId(Long propertyId);

    @Query("SELECT s FROM PropertyPackageSubscription s WHERE s.property.id = :propertyId AND s.status = :status")
    List<PropertyPackageSubscription> findByPropertyIdAndStatus(@Param("propertyId") Long propertyId,
                                                                  @Param("status") PackageSubscriptionStatus status);

    Optional<PropertyPackageSubscription> findByTransactionId(String transactionId);

    Long countByStatus(PackageSubscriptionStatus status);
}

