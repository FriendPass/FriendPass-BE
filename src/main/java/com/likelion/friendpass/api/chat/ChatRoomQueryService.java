package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatRoomInfoDto;
import com.likelion.friendpass.api.chat.dto.RoomTeammateDto;
import com.likelion.friendpass.api.user.dto.InterestTagResponse;
import com.likelion.friendpass.domain.chat.ChatRoom;
import com.likelion.friendpass.domain.chat.ChatRoomRepository;
import com.likelion.friendpass.domain.matching.MatchingMemberRepository;
import com.likelion.friendpass.domain.matching.MatchingTeamInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final MatchingMemberRepository matchingMemberRepository;
    private final MatchingTeamInterestRepository matchingTeamInterestRepository;

    @Transactional(readOnly = true)
    public ChatRoomInfoDto getRoomInfo(Long roomId, Long requesterId) {
        // 1) 방 조회
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId=" + roomId));

        Long teamId = room.getTeamId();

        // 2) 권한: 요청자가 이 팀의 멤버인지 확인(팀원이 아니면 403)
        boolean isMember = matchingMemberRepository
                .existsByTeam_TeamIdAndUser_UserId(teamId, requesterId);
        if (!isMember) {
            throw new SecurityException("해당 채팅방에 접근 권한이 없습니다.");
        }

        // 3) 공통 관심사(팀 관심사)
        var commonInterests = matchingTeamInterestRepository.findByTeam_TeamId(teamId)
                .stream()
                .map(mti -> InterestTagResponse.from(mti.getInterest()))
                .toList();

        // 4) 팀원 목록 (매칭된 멤버 전원)
        var teammates = matchingMemberRepository.findByTeam_TeamId(teamId)
                .stream()
                .map(mm -> new RoomTeammateDto(
                        mm.getUser().getUserId(),
                        mm.getUser().getNickname(),
                        mm.getUser().getProfileImage()
                ))
                .toList();

        // 5) 합쳐서 반환
        return ChatRoomInfoDto.builder()
                .roomId(room.getChatRoomId())
                .teamId(teamId)
                .roomName(room.getRoomName())
                .commonInterests(commonInterests)
                .teammates(teammates)
                .build();
    }

}
