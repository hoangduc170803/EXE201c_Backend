package com.stayease.repository;

import com.stayease.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByPropertyIdAndIsPublicTrueOrderByCreatedAtDesc(Long propertyId, Pageable pageable);
    
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Optional<Review> findByBookingId(Long bookingId);
    
    Boolean existsByBookingId(Long bookingId);
    
    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.property.id = :propertyId AND r.isPublic = true")
    BigDecimal getAverageRatingByProperty(@Param("propertyId") Long propertyId);
    
    Long countByPropertyIdAndIsPublicTrue(Long propertyId);
}

