package com.likelion.friendpass.domain.chat;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_room",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_chat_room_team", columnNames = "team_id")
        },
        indexes = {
                @Index(name = "ix_chat_room_team", columnList = "team_id"),
                @Index(name = "ix_chat_room_last", columnList = "last_message_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "room_name", nullable = false, length = 255)
    private String roomName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 목록 화면 최적화용 캐시 필드
    @Lob
    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (roomName == null) roomName = "Team " + teamId;
    }

    // 메시지 저장 시 호출
    public void touchLastMessage(String text, LocalDateTime at) {
        this.lastMessage = text;
        this.lastMessageAt = at;
    }
}

