package com.likelion.friendpass.domain.chat;

import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_message",
        indexes = {
                @Index(name = "ix_msg_room_id", columnList = "chat_room_id, chatmessage_id"), // 페이징 최적화
                @Index(name = "ix_msg_room_time", columnList = "chat_room_id, sent_at"),
                @Index(name = "ix_msg_team", columnList = "team_id"),
                @Index(name = "ix_msg_sender", columnList = "sender_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatmessage_id")
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId; // Team 엔티티 만들 때 @ManyToOne으로 교체 가능

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_msg_room"))
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_msg_sender"))
    private User sender;

    @Lob
    @Column(name = "text", nullable = false)
    private String text;   // 원문 메시지

    @Lob
    @Column(name = "translated_text")
    private String translatedText;   // 번역 메시지 (NULL 허용)

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @PrePersist
    void onCreate() {
        if (sentAt == null) sentAt = LocalDateTime.now();
    }

    // ★ 게으른 캐시 저장을 위한 최소 변경 지점
    public void applyTranslation(String translated) {
        this.translatedText = translated;
    }
}
