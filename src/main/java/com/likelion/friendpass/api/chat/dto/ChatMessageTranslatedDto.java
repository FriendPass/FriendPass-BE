package com.likelion.friendpass.api.chat.dto;

import com.likelion.friendpass.domain.chat.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageTranslatedDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String text;            // 원문
    private String translatedText;  // 번역문
    private String targetLang;      // 요청 타깃 언어
    private String sourceLang;      // 자동 감지된 원문 언어(정보용)
    private LocalDateTime sentAt;

    public static ChatMessageTranslatedDto of(ChatMessage m, String translated, String targetLang, String sourceLang) {
        return ChatMessageTranslatedDto.builder()
                .id(m.getId())
                .roomId(m.getChatRoom().getChatRoomId())
                .senderId(m.getSender().getUserId())
                .text(m.getText())
                .translatedText(translated)
                .targetLang(targetLang)
                .sourceLang(sourceLang)
                .sentAt(m.getSentAt())
                .build();
    }
}