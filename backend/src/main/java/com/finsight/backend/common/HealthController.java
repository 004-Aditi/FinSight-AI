package com.finsight.backend.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finsight.backend.common.response.ApiResponse;

@RestController
public class HealthController {
    @GetMapping("/")
    public String home() {
        return "FinSight AI backend is running";
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success(
                "FinSight backend running",
                "OK");
    }

}