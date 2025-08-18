package com.likelion.friendpass.api.chat.dto;

import com.likelion.friendpass.domain.chat.ChatRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomListDto {
    private Long roomId;
    private String roomName;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private boolean current;

    public static ChatRoomListDto of(ChatRoom r, boolean current){
        return ChatRoomListDto.builder()
                .roomId(r.getChatRoomId())
                .roomName(r.getRoomName())
                .lastMessage(r.getLastMessage())
                .lastMessageAt(r.getLastMessageAt())
                .current(current)
                .build();
    }
}
