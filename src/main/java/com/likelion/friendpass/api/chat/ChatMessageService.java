package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatMessageDto;
import com.likelion.friendpass.domain.chat.ChatMessage;
import com.likelion.friendpass.domain.chat.ChatMessageRepository;
import com.likelion.friendpass.domain.chat.ChatRoom;
import com.likelion.friendpass.domain.chat.ChatRoomMemberRepository;
import com.likelion.friendpass.domain.chat.ChatRoomRepository;
import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository msgRepo;
    private final ChatRoomRepository roomRepo;
    private final ChatRoomMemberRepository memberRepo;

    @PersistenceContext
    private EntityManager em;

    /** 메시지 저장 + 채팅방 마지막 메시지 캐시 갱신 */
    @Transactional
    public ChatMessageDto send(Long roomId, Long senderId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }

        ChatRoom room = roomRepo.findById(roomId).orElseThrow();
        User senderRef = em.getReference(User.class, senderId);

        // 권한 체크: 해당 유저가 방 멤버인지
        if (!memberRepo.existsByChatRoomAndUser(room, senderRef)) {
            throw new IllegalStateException("사용자는 이 방의 멤버가 아닙니다.");
        }

        ChatMessage saved = msgRepo.save(ChatMessage.builder()
                .chatRoom(room)
                .teamId(room.getTeamId())
                .sender(senderRef)
                .text(text)
                .sentAt(LocalDateTime.now())
                .build());

        // 목록 캐시 갱신 (같은 트랜잭션)
        room.touchLastMessage(saved.getText(), saved.getSentAt());

        return ChatMessageDto.from(saved);
    }
}
