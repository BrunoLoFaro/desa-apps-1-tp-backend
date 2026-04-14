package com.example.desabackend.service;

import com.example.desabackend.dto.UserDto;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.entity.UserRole;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public UserDto changeRole(Long userId, String roleName) {
        UserRole newRole;
        try {
            newRole = UserRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid role: " + roleName + ". Valid roles: TRAVELER, GUIDE, ADMIN");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        log.info("Changing role of user {} ({}) from {} to {}",
                userId, user.getEmail(), user.getRole(), newRole);

        user.setRole(newRole);
        UserEntity saved = userRepository.save(user);
        return toDto(saved);
    }

    private UserDto toDto(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                user.getRole().name(),
                user.getEnabled()
        );
    }
}
