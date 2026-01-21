package com.stayease.repository;

import com.stayease.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);
    
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
    
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
    
    @Query("SELECT f.property.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findPropertyIdsByUserId(@Param("userId") Long userId);
}

