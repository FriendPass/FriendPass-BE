package com.likelion.friendpass.api.user.dto;

import com.likelion.friendpass.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;
    private String nationality;
    private Boolean isExchange;
    private String language;
    private String profileImage;
    private Boolean isActive;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.nationality = user.getNationality();
        this.isExchange = user.getIsExchange();
        this.language = user.getLanguage();
        this.profileImage = user.getProfileImage();
        this.isActive = user.getIsActive();
    }
}
