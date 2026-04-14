package com.example.desabackend.controller;

import com.example.desabackend.dto.ChangeRoleRequestDto;
import com.example.desabackend.dto.UserDto;
import com.example.desabackend.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * List all users. Only ADMIN.
     */
    @GetMapping("/users")
    public List<UserDto> listUsers() {
        return adminService.listUsers();
    }

    /**
     * Change a user's role. Only ADMIN.
     * Body: { "role": "GUIDE" }
     */
    @PutMapping("/users/{id}/role")
    public UserDto changeUserRole(@PathVariable Long id,
                                  @Valid @RequestBody ChangeRoleRequestDto request) {
        return adminService.changeRole(id, request.role());
    }
}
