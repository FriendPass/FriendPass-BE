package com.likelion.friendpass.api.certification.dto;

public record CertificationRequest(
        Double latitude,
        Double longitude,
        Long userId
) {}
