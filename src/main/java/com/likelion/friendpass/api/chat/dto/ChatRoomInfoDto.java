package com.likelion.friendpass.api.chat.dto;

import com.likelion.friendpass.api.user.dto.InterestTagResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatRoomInfoDto {
    private Long roomId;
    private Long teamId;
    private String roomName;

    // 채팅방(=팀) 기준 공통 관심사 + 팀원 목록
    private List<InterestTagResponse> commonInterests;
    private List<RoomTeammateDto> teammates;
}
