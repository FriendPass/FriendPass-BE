package com.likelion.friendpass.api.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.likelion.friendpass.domain.user.User;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long userId,
        String email,
        String nickname,
        Boolean isExchange,
        String language,
        String profileImage,
        String nationalityCode,
        String nationalityNameKo,
        Long schoolId,
        String schoolName,
        List<String> interests
) {
    public static UserResponse from(User user, List<String> interests) {
        var n = user.getNationality();
        var s = user.getSchool();
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getIsExchange(),
                user.getLanguage(),
                user.getProfileImage(),
                n != null ? n.getCode()   : null,
                n != null ? n.getNameKo() : null,
                s != null ? s.getSchoolId() : null,
                s != null ? s.getSchoolName() : null,
                interests
        );
    }
}
