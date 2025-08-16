package com.likelion.friendpass.api.user.dto;

import com.likelion.friendpass.domain.interest.InterestTag;

public record InterestTagResponse(Long id, String name) {
    public static InterestTagResponse from(InterestTag it) {
        return new InterestTagResponse(it.getId(), it.getName());
    }
}