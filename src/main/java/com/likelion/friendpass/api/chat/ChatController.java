package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatRoomListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor

public class ChatController {
    private final ChatQueryService chatQueryService;

    @GetMapping("/rooms")
    public Map<String, List<ChatRoomListDto>> rooms(
            @AuthenticationPrincipal Long userId
    ){
        return chatQueryService.getMyRooms(userId);
    }
}
