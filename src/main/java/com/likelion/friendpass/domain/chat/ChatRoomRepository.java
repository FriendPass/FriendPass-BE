package com.likelion.friendpass.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByTeamIdOrderByChatRoomIdAsc(Long teamId);
    Optional<ChatRoom> findByTeamId(Long teamId); // 팀당 1방 정책이면 사용

    @Query("""
       select r from ChatRoom r
       join ChatRoomMember m on m.chatRoom = r
       where m.user.userId = :userId and m.isCurrent = true
       order by coalesce(r.lastMessageAt, r.createdAt) desc
    """)
    List<ChatRoom> findCurrentRoomsByUser(@Param("userId") Long userId);

    @Query("""
       select r from ChatRoom r
       join ChatRoomMember m on m.chatRoom = r
       where m.user.userId = :userId and m.isCurrent = false
       order by coalesce(r.lastMessageAt, r.createdAt) desc
    """)
    List<ChatRoom> findPastRoomsByUser(@Param("userId") Long userId);
}
