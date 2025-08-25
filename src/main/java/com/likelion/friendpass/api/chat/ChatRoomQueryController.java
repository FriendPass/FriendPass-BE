package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatRoomInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms") // 프론트 경로와 동일하게!
public class ChatRoomQueryController {

    private final ChatRoomQueryService chatRoomQueryService;

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomInfoDto> getRoomInfo(@PathVariable Long roomId,
                                                       Authentication authentication) {
        Long requesterId = extractUserId(authentication);
        ChatRoomInfoDto dto = chatRoomQueryService.getRoomInfo(roomId, requesterId);
        return ResponseEntity.ok(dto);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new SecurityException("인증 정보가 없습니다.");
        }
        Object principal = authentication.getPrincipal();
        // JwtAuthFilter에서 Long userId를 principal로 넣고 있음
        if (principal instanceof Long l) return l;

        // 혹시 다른 타입으로 바뀌어도 방어적으로 처리
        try {
            return Long.valueOf(principal.toString());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("인증 주체에서 userId를 파싱할 수 없습니다: " + principal);
        }
    }
}
