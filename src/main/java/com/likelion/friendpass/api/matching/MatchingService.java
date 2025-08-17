package com.likelion.friendpass.api.matching;

import com.likelion.friendpass.api.matching.dto.*;
import com.likelion.friendpass.domain.interest.UserInterestRepository;
import com.likelion.friendpass.domain.matching.*;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRequestRepository matchingRequestRepository;
    private final MatchingMemberRepository matchingMemberRepository;
    private final MatchingTeamRepository matchingTeamRepository;
    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final MatchingTeamInterestRepository matchingTeamInterestRepository;

    // 유저 관심사 조회 (이름)
    private List<String> getUserInterestNames(Long userId) {
        return userInterestRepository.findNamesByUserId(userId);
    }

    // 팀 대표 관심사 조회 (이름)
    private List<String> getTeamInterestNames(Long teamId) {
        return matchingTeamInterestRepository.findByTeam_TeamId(teamId)
                .stream()
                .map(mt ->mt.getInterest().getName())
                .toList();
    }

    // 매칭 상태 헬퍼
    private MatchingStatusResponse buildStatusResponse(MatchingStatus status,
                                                       MatchingRegion region,
                                                       List<String> interests) {
        MatchingStatusResponse response = new MatchingStatusResponse();
        response.setStatus(status);
        response.setRegion(region);
        response.setSelectedInterests(interests);
        return response;
    }

    // 매칭 화면 (신청 전, 대기중) = 매개변수가 userId, 서버에서 알고 있을 때, 새로 받을 게 없음 (dto로 안함)
    public MatchingStatusResponse getWaitingStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        MatchingRequest request = matchingRequestRepository
                .findByUserAndStatus(user, MatchingStatus.대기중)
                .orElse(null);

        List<String> selectedInterests = getUserInterestNames(userId);

        // 대기중인 경우
        if (request != null) {
            return buildStatusResponse(request.getStatus(), request.getRegion(), selectedInterests);
        } else { // 매칭 전 혹은 매칭 실패한 경우
            return buildStatusResponse(null, null, selectedInterests);
        }

    }

    // 매칭 신청하기 = dto가 필요하므로 dto로 함 (지역 받아야 해서)
    @Transactional
    public void createMatchingRequest(final MatchingRequestCreate dto) {
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

        return team;
    }

    // 완료된 화면
    public MatchingCompleteResponse getMatchingComplete(Long teamId) {

        // 팀 조회
        MatchingTeam team = matchingTeamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        // 팀원 조회
        List<MatchingMember> members = matchingMemberRepository.findByTeam(team);
        List<MatchingMemberResponse> memberDtos = members.stream()
                .map(m -> new MatchingMemberResponse(
                        m.getUser().getUserId(),
                        m.getUser().getNickname()
                ))
                .toList();

        // 대표 관심사 조회
        List<String> represents = getTeamInterestNames(teamId);

        // 완료 상태 조회
        MatchingStatusResponse statusDto = buildStatusResponse(
                MatchingStatus.수락,
                team.getMatchedRegion(),
                represents
        );

        // 최종 완료 DTO 생성 및 반환
        MatchingCompleteResponse completeDto = new MatchingCompleteResponse();
        completeDto.setTeamId(team.getTeamId());
        completeDto.setStatus(statusDto);
        completeDto.setMembers(memberDtos);
        completeDto.setRepresentativeInterests(represents);

        return completeDto;

    }
}
