package com.likelion.friendpass.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLanguageRequest {

        @NotBlank
        @Pattern(regexp = "ko|en", message = "language must be 'ko' or 'en'")
        private String language;
}