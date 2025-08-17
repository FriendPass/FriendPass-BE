package com.likelion.friendpass.api.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String nickname,
        @NotBlank String nationalityCode,
        @NotNull Boolean isExchange,
        @NotBlank String language,
        @NotNull Long schoolId
) {}