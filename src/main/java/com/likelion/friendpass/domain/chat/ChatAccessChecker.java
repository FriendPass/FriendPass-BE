package com.likelion.friendpass.domain.chat;

public interface ChatAccessChecker {
    void ensureBothMembers(Long userAId, Long userBId, Long chatId);
    void ensureMessageBelongsToChat(Long messageId, Long chatId);
}