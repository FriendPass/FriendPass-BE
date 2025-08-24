package com.likelion.friendpass.api.rank.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardItemDto {

    private String id;     // "u{userId}" 또는 "me01" (현재 로그인 유저일 경우)
    private long rank;     // 순위 (1-base)
    private String name;   // 닉네임
    private int stamp;     // 총 스탬프 개수
    private boolean isMe;  // 현재 로그인한 유저 여부
}
