package com.stayease.controller;

import com.stayease.dto.response.ApiResponse;
import com.stayease.dto.response.CategoryResponse;
import com.stayease.model.Category;
import com.stayease.repository.CategoryRepository;
import com.stayease.utils.PropertyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management API")
public class CategoryController {
    
    private final CategoryRepository categoryRepository;
    private final PropertyMapper propertyMapper;
    
    public CategoryController(CategoryRepository categoryRepository, PropertyMapper propertyMapper) {
        this.categoryRepository = categoryRepository;
        this.propertyMapper = propertyMapper;
    }
    
    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(propertyMapper::toCategoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/with-count")
    @Operation(summary = "Get categories with property count")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesWithCount() {
        List<Object[]> results = categoryRepository.findAllWithPropertyCount();
        List<CategoryResponse> categories = results.stream()
                .map(row -> {
                    CategoryResponse dto = propertyMapper.toCategoryResponse((Category) row[0]);
                    dto.setPropertyCount((Long) row[1]);
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}

