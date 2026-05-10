package com.finsight.backend.auth;

import com.finsight.backend.auth.dto.AuthResponse;
import com.finsight.backend.auth.dto.LoginRequest;
import com.finsight.backend.auth.dto.SignupRequest;
import com.finsight.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.finsight.backend.auth.dto.UserProfileResponse;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<AuthResponse> signup(
            @Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(
                "User registered successfully",
                authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(
                "User logged in successfully",
                authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.success(
                "User profile fetched successfully",
                new UserProfileResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()));
    }
}