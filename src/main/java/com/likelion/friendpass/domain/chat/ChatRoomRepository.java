package com.likelion.friendpass.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByTeamIdOrderByChatRoomIdAsc(Long teamId);
    Optional<ChatRoom> findByTeamId(Long teamId); // 팀당 1방 정책이면 사용
}
