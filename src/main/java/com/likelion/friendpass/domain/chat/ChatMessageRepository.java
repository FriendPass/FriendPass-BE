package com.likelion.friendpass.domain.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 목록/미리보기에 쓸 마지막 메시지 1건
    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom room);

    // 페이징: id 커서 기반 (이전 히스토리)
    @Query("""
       select m
       from ChatMessage m
       where m.chatRoom.chatRoomId = :roomId
         and (:beforeId is null or m.id < :beforeId)
       order by m.id desc
    """)
    List<ChatMessage> findPageByRoom(
            @Param("roomId") Long roomId,
            @Param("beforeId") Long beforeId,
            Pageable pageable);
}
