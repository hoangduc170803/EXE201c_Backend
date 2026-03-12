package com.stayease.service;

import com.stayease.config.JwtTokenProvider;
import com.stayease.dto.request.LoginRequest;
import com.stayease.dto.request.RegisterRequest;
import com.stayease.dto.response.AuthResponse;
import com.stayease.dto.response.UserResponse;
import com.stayease.enums.RoleName;
import com.stayease.exception.BadRequestException;
import com.stayease.exception.ConflictException;
import com.stayease.model.Role;
import com.stayease.model.User;
import com.stayease.repository.RoleRepository;
import com.stayease.repository.UserRepository;
import com.stayease.utils.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationInMs())
                .user(userMapper.toResponse(user))
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        boolean isHost = "HOST".equalsIgnoreCase(request.getRole());

        // Validate required HOST fields
        if (isHost) {
            if (request.getPhone() == null || request.getPhone().isBlank()) {
                throw new BadRequestException("Phone number is required for host registration");
            }
            if (request.getDateOfBirth() == null || request.getDateOfBirth().isBlank()) {
                throw new BadRequestException("Date of birth is required for host registration");
            }
            if (request.getAddress() == null || request.getAddress().isBlank()) {
                throw new BadRequestException("Address is required for host registration");
            }
            if (request.getCity() == null || request.getCity().isBlank()) {
                throw new BadRequestException("City is required for host registration");
            }
            if (request.getCountry() == null || request.getCountry().isBlank()) {
                throw new BadRequestException("Country is required for host registration");
            }
        }

        // Parse dateOfBirth safely
        LocalDate parsedDob = null;
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            try {
                parsedDob = LocalDate.parse(request.getDateOfBirth());
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid date of birth format. Expected: yyyy-MM-dd");
            }
        }

        // Resolve role: HOST → ROLE_HOST, GUEST → ROLE_USER
        RoleName roleName = isHost ? RoleName.ROLE_HOST : RoleName.ROLE_USER;
        Role assignedRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .dateOfBirth(parsedDob)
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .isActive(true)
                .isVerified(false)
                .isHost(isHost)
                .build();

        user.addRole(assignedRole);
        User savedUser = userRepository.save(user);

        // Auto login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationInMs())
                .user(userMapper.toResponse(savedUser))
                .build();
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return userMapper.toResponse(user);
    }
}


