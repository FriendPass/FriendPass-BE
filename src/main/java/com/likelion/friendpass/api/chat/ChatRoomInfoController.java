package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatRoomInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomInfoController {

    private final ChatRoomQueryService chatRoomQueryService;

    /**
     * 채팅방 정보 조회 (팀 공통 관심사 + 팀원 목록)
     * GET /api/chat/rooms/{roomId}
     */
    @GetMapping("/rooms/{roomId}")
    public ChatRoomInfoDto getRoomInfo(
            @PathVariable Long roomId,
            @AuthenticationPrincipal Long userId
    ) {
        return chatRoomQueryService.getRoomInfo(roomId, userId);
    }
}

