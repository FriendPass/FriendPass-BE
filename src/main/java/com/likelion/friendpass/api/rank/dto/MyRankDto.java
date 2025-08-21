package com.likelion.friendpass.api.rank.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MyRankDto {
    private Long userId;
    private String nickname;
    private String profileImage;
    private int totalStamps;
    private long rank; // 1-base
}
