package com.likelion.friendpass.domain.chat;

import com.likelion.friendpass.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findAllByChatRoomOrderByIdAsc(ChatRoom chatRoom);
    List<ChatRoomMember> findAllByUserOrderByIdDesc(User user);
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
    long countByChatRoom(ChatRoom chatRoom);
}
