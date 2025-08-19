package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatMessageDto;
import com.likelion.friendpass.api.chat.dto.SendMessageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate template;

    /** 클라이언트 SEND: /app/rooms/{roomId}  →  서버 브로드캐스트: /topic/rooms/{roomId} */
    @MessageMapping("/rooms/{roomId}")
    public void sendToRoom(
            @DestinationVariable Long roomId,
            @Payload SendMessageReq req,
            Principal principal // CONNECT 시 설정한 user-id가 들어있음
    ) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalStateException("인증되지 않은 WebSocket 연결입니다.");
        }

        Long senderId;
        try {
            // UsernamePasswordAuthenticationToken의 principal을 Long으로 넣지 않고 name에 문자열로 올 수도 있어
            // interceptor에서 auth.getPrincipal()에 Long을 넣었으므로 아래처럼 name 대신 캐스팅이 필요할 수 있음.
            // 가장 안전하게는 name을 Long으로 파싱 시도:
            senderId = Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            // principal이 UsernamePasswordAuthenticationToken인 경우 getPrincipal() 직접 캐스팅
            Object p = ((org.springframework.security.core.Authentication) principal).getPrincipal();
            senderId = (p instanceof Long) ? (Long) p : null;
        }
        if (senderId == null) {
            throw new IllegalStateException("보낸 사람 식별에 실패했습니다.");
        }

        ChatMessageDto dto = chatMessageService.send(roomId, senderId, req.getText());
        template.convertAndSend("/topic/rooms/" + roomId, dto);
    }
}
