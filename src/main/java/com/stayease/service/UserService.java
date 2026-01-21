package com.stayease.service;

import com.stayease.controller.request.UpdateUserRequest;
import com.stayease.controller.response.UserResponse;
import com.stayease.exception.ResourceNotFoundException;
import com.stayease.model.Role;
import com.stayease.model.User;
import com.stayease.repository.RoleRepository;
import com.stayease.repository.UserRepository;
import com.stayease.utils.UserMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    
    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getBio() != null) user.setBio(request.getBio());
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
    
    @Transactional
    public UserResponse becomeHost(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Role hostRole = roleRepository.findByName(Role.RoleName.ROLE_HOST)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_HOST"));
        
        user.setIsHost(true);
        user.addRole(hostRole);
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
    
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}

