package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatRoomListDto;
import com.likelion.friendpass.domain.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class ChatQueryService {
    private final ChatRoomRepository roomRepo;

    public Map<String, List<ChatRoomListDto>> getMyRooms(Long userId){
        var current = roomRepo.findCurrentRoomsByUser(userId)
                .stream().map(r -> ChatRoomListDto.of(r, true)).toList();
        var past = roomRepo.findPastRoomsByUser(userId)
                .stream().map(r -> ChatRoomListDto.of(r, false)).toList();
        return Map.of("current", current, "past", past);
    }
}
