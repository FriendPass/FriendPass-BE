package com.likelion.friendpass.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMessageTranslationRepository extends JpaRepository<ChatMessageTranslation, Long> {
    Optional<ChatMessageTranslation> findByMessageAndTargetLang(ChatMessage message, String targetLang);
}