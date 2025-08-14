package com.likelion.friendpass.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailRequest(
        @Email @NotBlank String email
) {}