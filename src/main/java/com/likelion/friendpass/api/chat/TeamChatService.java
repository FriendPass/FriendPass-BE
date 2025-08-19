package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.domain.chat.ChatRoom;
import com.likelion.friendpass.domain.chat.ChatRoomMember;
import com.likelion.friendpass.domain.chat.ChatRoomMemberRepository;
import com.likelion.friendpass.domain.chat.ChatRoomRepository;
import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * 팀 채팅방이 없으면 생성하고, 멤버(User)들을 참가자로 등록한다.
     * - 멱등 보장: 여러 번 호출돼도 방은 1개, 멤버는 중복 없이 유지.
     *
     * @param teamId         팀 ID
     * @param memberUserIds  팀원 user_id 목록
     * @return 생성되었거나 존재하는 채팅방의 ID
     */
    @Transactional
    public Long ensureTeamRoom(Long teamId, List<Long> memberUserIds) {
        // 1) 팀당 방 1개 보장: 없으면 생성, 있으면 가져오기
        ChatRoom room = chatRoomRepository.findByTeamId(teamId)
                .orElseGet(() -> createRoom(teamId));

        // 2) 멤버 등록(중복 방지)
        for (Long userId : memberUserIds) {
            // User 엔티티 전체를 조회하지 않고 FK 참조만 필요 → getReference 사용
            User userRef = em.getReference(User.class, userId);

            // (chat_room_id, user_id) 유니크를 코드 레벨에서도 체크
            if (!chatRoomMemberRepository.existsByChatRoomAndUser(room, userRef)) {
                chatRoomMemberRepository.save(
                        ChatRoomMember.builder()
                                .chatRoom(room)
                                .user(userRef)
                                .teamId(teamId)
                                .isCurrent(true)
                                .build()
                );
            }
        }
        return room.getChatRoomId();
    }

    /** team_id 기준으로 채팅방 신규 생성. 동시성 충돌 시 재조회. */
    private ChatRoom createRoom(Long teamId) {
        try {
            return chatRoomRepository.save(
                    ChatRoom.builder()
                            .teamId(teamId)
                            .roomName("Team " + teamId)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            // UNIQUE(team_id) 제약 충돌: 이미 다른 트랜잭션이 만들었음 → 재조회
            return chatRoomRepository.findByTeamId(teamId).orElseThrow();
        }
    }
}
