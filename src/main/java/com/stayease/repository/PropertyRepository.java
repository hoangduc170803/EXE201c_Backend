package com.stayease.repository;

import com.stayease.model.Property;
import com.stayease.model.Property.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    
    Page<Property> findByStatusAndIsDeletedFalse(PropertyStatus status, Pageable pageable);
    
    Page<Property> findByHostIdAndIsDeletedFalse(Long hostId, Pageable pageable);
    
    Page<Property> findByCategoryIdAndStatusAndIsDeletedFalse(Long categoryId, PropertyStatus status, Pageable pageable);
    
    Page<Property> findByCityIgnoreCaseAndStatusAndIsDeletedFalse(String city, PropertyStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.status = :status AND p.isDeleted = false AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.city) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Property> searchProperties(@Param("keyword") String keyword, 
                                    @Param("status") PropertyStatus status, 
                                    Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.isFeatured = true AND p.status = 'ACTIVE' AND p.isDeleted = false " +
           "ORDER BY p.averageRating DESC")
    List<Property> findFeaturedProperties(Pageable pageable);
    
    @Modifying
    @Query("UPDATE Property p SET p.viewCount = p.viewCount + 1 WHERE p.id = :propertyId")
    void incrementViewCount(@Param("propertyId") Long propertyId);
    
    @Query("SELECT DISTINCT p.city FROM Property p WHERE p.status = 'ACTIVE' AND p.isDeleted = false ORDER BY p.city")
    List<String> findDistinctCities();

    @Query("""
        SELECT DISTINCT p FROM Property p 
        LEFT JOIN p.amenities a 
        WHERE p.status = 'ACTIVE' AND p.isDeleted = false
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND (:minPrice IS NULL OR p.pricePerNight >= :minPrice)
        AND (:maxPrice IS NULL OR p.pricePerNight <= :maxPrice)
        AND (:propertyType IS NULL OR p.propertyType = :propertyType)
        AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:isInstantBook IS NULL OR p.isInstantBook = :isInstantBook)
        AND (:minGuests IS NULL OR p.maxGuests >= :minGuests)
        AND (COALESCE(:amenityIds, NULL) IS NULL OR a.id IN :amenityIds)
        GROUP BY p.id
        HAVING COALESCE(:amenitySize, 0) = 0 OR COUNT(DISTINCT a.id) >= :amenitySize
    """)
    Page<Property> filterProperties(
        @Param("categoryId") Long categoryId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("propertyType") String propertyType,
        @Param("city") String city,
        @Param("isInstantBook") Boolean isInstantBook,
        @Param("minGuests") Integer minGuests,
        @Param("amenityIds") List<Long> amenityIds,
        @Param("amenitySize") Long amenitySize,
        Pageable pageable
    );
}

