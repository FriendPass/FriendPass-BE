package com.likelion.friendpass.api.matching;

import com.likelion.friendpass.api.chat.TeamChatService;
import com.likelion.friendpass.api.matching.dto.MatchingCompleteDto;
import com.likelion.friendpass.api.matching.dto.MatchingMemberDto;
import com.likelion.friendpass.api.matching.dto.MatchingRequestCreateDto;
import com.likelion.friendpass.api.matching.dto.MatchingStatusDto;
import com.likelion.friendpass.domain.matching.*;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRequestRepository matchingRequestRepository;
    private final MatchingMemberRepository matchingMemberRepository;
    private final MatchingTeamRepository matchingTeamRepository;
    private final UserRepository userRepository;
    private final TeamChatService teamChatService;

    // 매칭 화면 (신청 전, 대기중) = 매개변수가 userId, 서버에서 알고 있을 때, 새로 받을 게 없음 (dto로 안함)
    public MatchingStatusDto getWaitingStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        MatchingRequest request = matchingRequestRepository
                .findByUserAndStatus(user, MatchingStatus.대기중)
                .orElse(null);

        MatchingStatusDto statusDto = new MatchingStatusDto();

        // 대기중인 경우
        if (request != null) {
            statusDto.setStatus(request.getStatus());
            statusDto.setRegion(request.getRegion());
            statusDto.setInterests(new ArrayList<>()); // 나중에 수정
        } else { // 신청 전인 경우
            statusDto.setStatus(null);
            statusDto.setRegion(null);
            statusDto.setInterests(new ArrayList<>()); // 나중에 수정
        }

        return statusDto;
    }

    // 매칭 신청하기 = dto가 필요하므로 dto로 함 (지역 받아야 해서)
    @Transactional
    public void createMatchingRequest(final MatchingRequestCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        MatchingRequest request = MatchingRequest.builder()
                .user(user)
                .region(dto.getRegion())
                .status(MatchingStatus.대기중)
                .requestedAt(LocalDateTime.now())
                .build();

        matchingRequestRepository.save(request);
    }

    // 매칭 신청 중 같은 지역을 매칭한 신청만 정렬
    public List<MatchingRequest> getMatchingRequestsByRegion(MatchingRegion region) {
        List<MatchingRequest> waitingRequests = matchingRequestRepository.findByRegionAndStatus(region, MatchingStatus.대기중);

        if (waitingRequests.isEmpty()) {
            throw new RuntimeException("해당 지역에 대기 중인 요청이 없습니다.");
        }

        return waitingRequests;
    }

    // 매칭하기
    public MatchingTeam createMatchingTeam(MatchingRegion region) {
        List<MatchingRequest> waitingRequests = getMatchingRequestsByRegion(region);

        if(waitingRequests.size() < 4) {
            throw new RuntimeException("팀을 만들기 위한 충분한 매칭 신청이 없습니다.");
        }

        // 임시 4명 (나중에 관심사 기반 유사도 로직 구현)
        List<MatchingRequest> selectedRequests = waitingRequests.subList(0,4);

        MatchingTeam team = MatchingTeam.builder()
                .matchedRegion(region)
                .matchedAt(LocalDateTime.now())
                .build();
        matchingTeamRepository.save(team);

        for (MatchingRequest request : selectedRequests) {
            // 팀원 객체 생성
            MatchingMember member = new MatchingMember();
            member.setTeam(team);
            member.setUser(request.getUser());
            matchingMemberRepository.save(member);

            // 매칭 요청 상태 업데이트
            request.setTeam(team);
            request.setStatus(MatchingStatus.수락);
        }

        // ----------  여기부터 채팅방 자동 생성(멱등) ----------
        // 팀원 user_id 목록만 뽑아서 전달 (User 재조회 불필요)
        List<Long> memberUserIds = selectedRequests.stream()
                .map(req -> req.getUser().getUserId())
                .collect(Collectors.toList());

        // 팀당 1개 채팅방 보장 + 멤버 등록
        teamChatService.ensureTeamRoom(team.getTeamId(), memberUserIds);
        // ----------  끝 ----------

        return team;
    }

    // 완료된 화면
    public MatchingCompleteDto getMatchingComplete(Long teamId) {

        // 팀 조회
        MatchingTeam team = matchingTeamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        // 팀원 조회
        List<MatchingMember> members = matchingMemberRepository.findByTeam(team);
        List<MatchingMemberDto> memberDtos = members.stream()
                .map(m -> new MatchingMemberDto (
                        m.getUser().getUserId(),
                        m.getUser().getNickname()
                ))
                .collect(Collectors.toList());

        // 상태 조회
        MatchingStatusDto statusDto = new MatchingStatusDto();
        statusDto.setStatus(MatchingStatus.수락);
        statusDto.setRegion(team.getMatchedRegion());
        statusDto.setInterests(new ArrayList<>()); // 임시 관심사 리스트 추후 db 생성시 추가

        // 최종 완료 DTO 생성 및 반환
        MatchingCompleteDto completeDto = new MatchingCompleteDto();
        completeDto.setTeamId(team.getTeamId());
        completeDto.setStatus(statusDto);
        completeDto.setMembers(memberDtos);
        completeDto.setInterests(new ArrayList<>()); // 임시 관심사 리스트 추후 db 생성시 추가

        return completeDto;


    }
}
