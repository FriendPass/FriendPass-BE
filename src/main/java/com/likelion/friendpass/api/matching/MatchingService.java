package com.likelion.friendpass.api.matching;

import com.likelion.friendpass.api.matching.dto.*;
import com.likelion.friendpass.api.place.dto.InterestPlaceResponse;
import com.likelion.friendpass.api.place.dto.PlaceResponse;
import com.likelion.friendpass.api.user.dto.InterestTagResponse;
import com.likelion.friendpass.domain.interest.InterestTag;
import com.likelion.friendpass.domain.interest.InterestTagRepository;
import com.likelion.friendpass.domain.interest.UserInterestRepository;
import com.likelion.friendpass.domain.matching.*;
import com.likelion.friendpass.domain.place.Place;
import com.likelion.friendpass.domain.place.PlaceRepository;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.likelion.friendpass.api.chat.TeamChatService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class MatchingService {
    private final MatchingRequestRepository matchingRequestRepository;
    private final MatchingMemberRepository matchingMemberRepository;
    private final MatchingTeamRepository matchingTeamRepository;
    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final MatchingTeamInterestRepository matchingTeamInterestRepository;
    private final PlaceRepository placeRepository;
    private final InterestTagRepository interestTagRepository;
    private final TeamChatService teamChatService;
    private static final Logger log = LoggerFactory.getLogger(MatchingService.class);
    // 유저 관심사 조회 (이름)
    private List<String> getUserInterestNames(Long userId) {
        return userInterestRepository.findNamesByUserId(userId);
    }

    // 팀 대표 관심사 조회 (Id + 이름)
    private List<InterestTagResponse> getTeamInterestTags(Long teamId) {
        return matchingTeamInterestRepository.findByTeam_TeamId(teamId)
                .stream()
                .map(mti -> InterestTagResponse.from(mti.getInterest()))
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
    public void createMatchingRequest(Long userId, MatchingRequestCreate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        MatchingRequest request = MatchingRequest.builder()
                .user(user)
                .region(dto.getRegion())
                .status(MatchingStatus.대기중)
                .requestedAt(LocalDateTime.now())
                .build();

        matchingRequestRepository.save(request);
    }

    // 매칭하기
    @Transactional
    public List<MatchingCompleteResponse> createMatchingTeam() {
        // 대기 중인 요청 가져오기
        List<MatchingRequest> waitingRequests = matchingRequestRepository.findByStatus(MatchingStatus.대기중);

        if (waitingRequests.size() < 4) {
            throw new RuntimeException("팀을 만들기 위한 충분한 매칭 신청이 없습니다.");
        }


        List<MatchingRequestForModel> userDtos = waitingRequests.stream()
                .map(req -> {
                    User user = req.getUser();
                    List<String> interests = getUserInterestNames(user.getUserId());
                    return new MatchingRequestForModel(
                            user.getUserId(),
                            user.getIsExchange() ? 1 : 0,
                            req.getRegion().name(),
                            interests
                    );
                })
                .toList();


        MatchingTeamRequest teamRequestDto = new MatchingTeamRequest(userDtos);

        WebClient webClient = WebClient.create("http://ai-container:8000");

        List<ConfirmedTeamResponse> confirmedTeams = webClient.post()
                .uri("/recommend-teams")
                .bodyValue(teamRequestDto.getUsers()) // 모델은 List<User> 형식으로 받음
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ConfirmedTeamResponse>>() {})
                .block(); // 동기 처리

        List<MatchingCompleteResponse> completeResponses = new ArrayList<>();

        // 추천된 팀별 처리
        for (ConfirmedTeamResponse confirmedTeam : confirmedTeams) {
            // 팀 생성 및 저장
            MatchingTeam team = MatchingTeam.builder()
                    .matchedRegion(MatchingRegion.valueOf(confirmedTeam.getMatchedRegion()))
                    .matchedAt(LocalDateTime.now())
                    .build();
            matchingTeamRepository.save(team);

            // ---------- [추가] 채팅방 자동 생성(멱등) ----------
            List<Long> memberUserIds = confirmedTeam.getMemberUserIds();
            // 팀당 1개 채팅방 보장 + 팀원 등록 (TeamChatService 내부에서 멱등 처리
            teamChatService.ensureTeamRoom(team.getTeamId(), memberUserIds);
            // ---------- [끝] ----------

            // 팀원 저장 및 매칭 요청 상태 업데이트
            for (Long userId : confirmedTeam.getMemberUserIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다: " + userId));

                MatchingMember member = new MatchingMember();
                member.setTeam(team);
                member.setUser(user);
                matchingMemberRepository.save(member);

                // 매칭 요청 상태 업데이트
                MatchingRequest request = matchingRequestRepository.findByUserAndStatus(user, MatchingStatus.대기중)
                        .orElseThrow(() -> new RuntimeException("매칭 요청이 존재하지 않습니다: " + userId));
                request.setTeam(team);
                request.setStatus(MatchingStatus.수락);
                matchingRequestRepository.save(request);
            }

            System.out.println("추천된 팀 수: " + confirmedTeams.size());
            if (confirmedTeam.getRepresentativeInterests() == null) {
                System.out.println("대표 관심사 맵이 null입니다!");
                throw new RuntimeException("대표 관심사 맵이 null이라서 처리 불가");
            }


            // 모델에서 받은 대표 관심사 이름 → DB에서 id 조회 → DTO 생성
            List<String> interestNames = confirmedTeam.getRepresentativeInterests(); // 이미 List<String>임

            List<InterestTagResponse> interestTags = interestNames.stream()
                    .map(name -> interestTagRepository.findByName(name)
                            .map(it -> new InterestTagResponse(it.getInterestId(), it.getName()))
                            .orElseThrow(() -> new RuntimeException("관심사를 찾을 수 없습니다: " + name))
                    ).toList();

            for (InterestTagResponse tag : interestTags) {
                InterestTag interest = interestTagRepository.findById(tag.InterestId())
                        .orElseThrow(() -> new RuntimeException("관심사 ID를 찾을 수 없습니다."));

                MatchingTeamInterest teamInterest = new MatchingTeamInterest();
                teamInterest.setTeam(team);
                teamInterest.setInterest(interest);
                matchingTeamInterestRepository.save(teamInterest);
            }
            MatchingRegion matchedRegion = MatchingRegion.valueOf(confirmedTeam.getMatchedRegion());


            // 관심사별 장소 조회
            List<InterestPlaceResponse> interestPlaces = interestTags.stream()
                    .map(tag -> {
                        List<Place> places = placeRepository.findByRegionAndInterest_InterestId(matchedRegion, tag.InterestId());
                        List<PlaceResponse> placeResponses = places.stream()
                                .map(p -> new PlaceResponse(p.getName(), p.getAddress(), p.getDescription()))
                                .toList();
                        return new InterestPlaceResponse(tag.name(), placeResponses);
                    })
                    .toList();

            List<MatchingMember> members = matchingMemberRepository.findByTeam(team);

            // 최종 DTO 생성
            List<MatchingMemberResponse> memberDtos = members.stream()
                    .map(m -> new MatchingMemberResponse(m.getUser().getUserId(), m.getUser().getNickname()))
                    .toList();

            MatchingStatusResponse statusDto = buildStatusResponse(MatchingStatus.수락, matchedRegion,
                    interestTags.stream().map(InterestTagResponse::name).toList());

            MatchingCompleteResponse completeDto = new MatchingCompleteResponse();
            completeDto.setTeamId(team.getTeamId());
            completeDto.setStatus(statusDto);
            completeDto.setMembers(memberDtos);
            completeDto.setRepresentativeInterests(interestTags);
            completeDto.setRepresentativePlaces(interestPlaces);

            completeResponses.add(completeDto);
        }

        return completeResponses;

    }


    // 완료된 화면
    public MatchingCompleteResponse getMatchingComplete(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 매칭 요청 조회
        MatchingRequest request = matchingRequestRepository.findByUserAndStatus(user, MatchingStatus.수락)
                .orElseThrow(() -> new RuntimeException("매칭 요청이 없습니다."));

        // 매칭 완료 여부 확인
        if (request.getStatus() != MatchingStatus.수락 || request.getTeam() == null) {
            throw new RuntimeException("아직 매칭되지 않았습니다.");
        }

        MatchingTeam team = request.getTeam();
        Long teamId = team.getTeamId();

        // 팀원 조회
        List<MatchingMember> members = matchingMemberRepository.findByTeam(team);
        List<MatchingMemberResponse> memberDtos = members.stream()
                .map(m -> new MatchingMemberResponse(
                        m.getUser().getUserId(),
                        m.getUser().getNickname()
                ))
                .toList();

        // 대표 관심사 조회
        List<InterestTagResponse> interestTags = getTeamInterestTags(teamId);

        // 관심사별 장소 묶기
        List<InterestPlaceResponse> interestPlaces = interestTags.stream()
                .map(tag -> {
                    List<Place> places = placeRepository.findByRegionAndInterest_InterestId(team.getMatchedRegion(), tag.InterestId());
                    List<PlaceResponse> placeResponses = places.stream()
                            .map(p -> new PlaceResponse(p.getName(), p.getAddress(), p.getDescription()))
                            .toList();
                    return new InterestPlaceResponse(tag.name(), placeResponses);
                })
                .toList();

        // 대표 관심사 이름만 추출
        List<String> interestNames = interestTags.stream()
                .map(InterestTagResponse::name)
                .toList();

        // 상태 DTO 생성
        MatchingStatusResponse statusDto = buildStatusResponse(
                MatchingStatus.수락,
                team.getMatchedRegion(),
                interestNames
        );

        // 최종 응답 DTO 생성
        MatchingCompleteResponse completeDto = new MatchingCompleteResponse();
        completeDto.setTeamId(teamId);
        completeDto.setStatus(statusDto);
        completeDto.setMembers(memberDtos);
        completeDto.setRepresentativeInterests(interestTags);
        completeDto.setRepresentativePlaces(interestPlaces);

        return completeDto;

    }

    // 매칭 나가기
    @Transactional
    public void exitMatching(Long teamId) {
        List<MatchingRequest> requests = matchingRequestRepository.findByTeam_TeamId(teamId);
        if (requests.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 팀입니다.");
        }
        for (MatchingRequest request : requests) {
            request.setStatus(MatchingStatus.만료);
        }
        matchingRequestRepository.saveAll(requests);
    }

}