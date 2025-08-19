package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatMessageDto;
import com.likelion.friendpass.api.chat.dto.SendMessageReq;
import com.likelion.friendpass.domain.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor

public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final ChatMessageRepository msgRepo;

    /** 메시지 전송 */
    @PostMapping("/rooms/{roomId}/messages")
    public ChatMessageDto send(
            @PathVariable Long roomId,
            @AuthenticationPrincipal Long senderId,
            @RequestBody SendMessageReq req
    ) {
        return chatMessageService.send(roomId, senderId, req.getText());
    }

    /** 메시지 조회 (커서 페이징) */
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageDto> list(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "50") int size
    ) {
        return msgRepo.findPageByRoom(roomId, beforeId, PageRequest.of(0, size))
                .stream().map(ChatMessageDto::from).toList();
    }
}
