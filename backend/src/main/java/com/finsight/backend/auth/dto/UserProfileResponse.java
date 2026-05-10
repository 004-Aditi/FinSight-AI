package com.finsight.backend.auth.dto;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email) {
}