package com.likelion.friendpass.domain.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_message_translation",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_msg_lang", columnNames = {"chat_message_id", "target_lang"})
        },
        indexes = {
                @Index(name = "ix_msgtr_msg", columnList = "chat_message_id"),
                @Index(name = "ix_msgtr_lang", columnList = "target_lang")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessageTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_translation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_message_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_msgtr_msg"))
    private ChatMessage message;

    @Column(name = "target_lang", nullable = false, length = 16)
    private String targetLang; // 예: "ko", "en", "ja", "zh-CN"

    @Lob
    @Column(name = "translated_text", nullable = false)
    private String translatedText;

    @Column(name = "source_lang", length = 16)
    private String sourceLang; // AWS가 감지한 원문 언어(정보 용)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
