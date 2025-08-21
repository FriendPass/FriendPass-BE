package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatMessageTranslatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageTranslationController {

    private final ChatMessageTranslationService translationService;

    /**
     * 메시지 번역 (요청 타깃 언어가 없으면 유저 설정 언어 사용)
     * 예: GET /api/chat/messages/123/translate         -> 사용자 설정 언어
     *     GET /api/chat/messages/123/translate?target=ja -> 일본어로
     */
    @GetMapping("/messages/{messageId}/translate")
    public ChatMessageTranslatedDto translateOne(
            @PathVariable Long messageId,
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String target
    ) {
        return translationService.translateForUser(messageId, userId, target);
    }
}
