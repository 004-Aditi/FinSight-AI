package com.finsight.backend.auth;

import com.finsight.backend.auth.dto.AuthResponse;
import com.finsight.backend.auth.dto.LoginRequest;
import com.finsight.backend.auth.dto.SignupRequest;
import com.finsight.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
}