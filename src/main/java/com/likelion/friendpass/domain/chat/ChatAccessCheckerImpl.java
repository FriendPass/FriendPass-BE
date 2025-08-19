package com.likelion.friendpass.domain.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatAccessCheckerImpl implements ChatAccessChecker {

    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void ensureBothMembers(Long userAId, Long userBId, Long chatId) {
        boolean a = chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(chatId, userAId);
        boolean b = chatRoomMemberRepository.existsByChatRoom_ChatRoomIdAndUser_UserId(chatId, userBId);
        if (!(a && b)) throw new AccessDeniedException("해당 채팅방 멤버가 아닙니다.");
    }

    @Override
    public void ensureMessageBelongsToChat(Long messageId, Long chatId) {
        if (messageId == null) return;
        boolean ok = chatMessageRepository.existsByIdAndChatRoom_ChatRoomId(messageId, chatId);
        if (!ok) throw new IllegalArgumentException("메시지가 채팅방에 속하지 않습니다.");
    }
}