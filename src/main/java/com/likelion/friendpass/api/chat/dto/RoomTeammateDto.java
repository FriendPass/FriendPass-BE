package com.likelion.friendpass.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomTeammateDto {
    private Long userId;
    private String nickname;
}

