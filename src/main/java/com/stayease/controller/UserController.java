package com.stayease.controller;

import com.stayease.controller.request.UpdateUserRequest;
import com.stayease.controller.response.ApiResponse;
import com.stayease.controller.response.UserResponse;
import com.stayease.service.CustomUserDetailsService.UserPrincipal;
import com.stayease.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", user));
    }
    
    @PostMapping("/become-host")
    @Operation(summary = "Become host")
    public ResponseEntity<ApiResponse<UserResponse>> becomeHost(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UserResponse user = userService.becomeHost(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("You are now a host!", user));
    }
}

