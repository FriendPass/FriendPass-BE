package com.likelion.friendpass.api.user.dto;

import com.likelion.friendpass.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String nationality;
    private Boolean isExchange;
    private String language;
    private String profileImage;
    private Boolean isActive;
    private Long schoolId;
    private List<String> interests;

    public static UserResponse from(User user, List<String> interests) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .nationality(user.getNationality())
                .isExchange(user.getIsExchange())
                .language(user.getLanguage())
                .profileImage(user.getProfileImage())
                .isActive(user.getIsActive())
                .schoolId(user.getSchool().getSchoolId())
                .interests(interests)
                .build();
    }
}