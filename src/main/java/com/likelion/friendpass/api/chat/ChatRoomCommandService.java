package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.domain.chat.ChatRoom;
import com.likelion.friendpass.domain.chat.ChatRoomMemberRepository;
import com.likelion.friendpass.domain.chat.ChatRoomRepository;
import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepository roomRepo;
    private final ChatRoomMemberRepository memberRepo;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void rename(Long roomId, Long userId, String newName) {
        String name = (newName == null) ? "" : newName.trim();
        if (name.isEmpty() || name.length() > 50) {
            throw new IllegalArgumentException("방 이름은 1~50자로 입력하세요.");
        }

        ChatRoom room = roomRepo.findById(roomId).orElseThrow();
        User userRef = em.getReference(User.class, userId);

        // 방 멤버만 변경 가능(정책에 따라 리더만 허용하도록 확장 가능)
        if (!memberRepo.existsByChatRoomAndUser(room, userRef)) {
            throw new IllegalStateException("해당 방의 멤버만 이름을 변경할 수 있습니다.");
        }

        room.rename(name); // Dirty Checking으로 업데이트
    }
}
