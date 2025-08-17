package com.likelion.friendpass.domain.chat;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_room",
        indexes = {
                @Index(name = "ix_chat_room_team", columnList = "team_id")
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
