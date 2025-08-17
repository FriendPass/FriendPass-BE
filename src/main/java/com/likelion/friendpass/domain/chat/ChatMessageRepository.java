package com.likelion.friendpass.domain.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderBySentAtDesc(ChatRoom chatRoom, Pageable pageable);
    List<ChatMessage> findByChatRoomAndIdLessThanOrderBySentAtDesc(ChatRoom chatRoom, Long lastId, Pageable pageable);
    long countByChatRoom(ChatRoom chatRoom);
}
