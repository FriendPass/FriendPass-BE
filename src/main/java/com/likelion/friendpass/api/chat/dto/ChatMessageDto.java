package com.likelion.friendpass.api.chat.dto;

import com.likelion.friendpass.domain.chat.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String text;
    private LocalDateTime sentAt;

    public static ChatMessageDto from(ChatMessage m) {
        return ChatMessageDto.builder()
                .id(m.getId())
                .roomId(m.getChatRoom().getChatRoomId())
                .senderId(m.getSender().getUserId())
                .text(m.getText())
                .sentAt(m.getSentAt())
                .build();
    }
}

