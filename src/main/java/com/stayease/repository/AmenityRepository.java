package com.stayease.repository;

import com.stayease.enums.AmenityCategory;
import com.stayease.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    
    List<Amenity> findByIsActiveTrueOrderByNameAsc();
    
    List<Amenity> findByCategoryAndIsActiveTrueOrderByNameAsc(AmenityCategory category);
    
    List<Amenity> findByIdIn(List<Long> ids);
    
    Boolean existsByNameIgnoreCase(String name);
}

