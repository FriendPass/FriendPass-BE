package com.likelion.friendpass.api.certification;

import com.likelion.friendpass.api.certification.dto.CertificationRequest;
import com.likelion.friendpass.api.certification.dto.CertificationResponse;
import com.likelion.friendpass.api.rank.RankService;
import com.likelion.friendpass.domain.certification.Certification;
import com.likelion.friendpass.domain.certification.CertificationRepository;
import com.likelion.friendpass.domain.matching.*;
import com.likelion.friendpass.domain.place.Place;
import com.likelion.friendpass.domain.place.PlaceRepository;
import com.likelion.friendpass.domain.rank.UserRewards;
import com.likelion.friendpass.domain.rank.UserRewardsRepository;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final MatchingTeamRepository matchingTeamRepository;
    private final MatchingMemberRepository matchingMemberRepository;
    private final PlaceRepository placeRepository;
    private final CertificationRepository certificationRepository;
    private final MatchingRequestRepository matchingRequestRepository;
    private final UserRepository userRepository;
    private final MatchingTeamInterestRepository matchingTeamInterestRepository;
    private final RankService rankService;

    public CertificationResponse certify(CertificationRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Double lat = request.latitude();
        Double lng = request.longitude();

        User user = userRepository.getReferenceById(userId);
        if (request.latitude() == null || request.longitude() == null) {
            throw new IllegalArgumentException("위치 정보가 누락되었습니다.");
        }
        // 팀 가져오기
        MatchingRequest matchingRequest = matchingRequestRepository
                .findByUserAndStatus(user, MatchingStatus.수락)
                .orElseThrow(() -> new RuntimeException("활성화된 매칭 요청 없음"));

        MatchingTeam team = matchingRequest.getTeam();

        List<MatchingTeamInterest> repInterests = matchingTeamInterestRepository
                .findByTeam_TeamId(team.getTeamId());


        // 팀의 대표 관심사로 가져온 대표 장소들 출력
        List<Place> places = repInterests.stream()
                .flatMap(repinterest -> placeRepository.findByRegionAndInterest_InterestId(
                        team.getMatchedRegion(),
                        repinterest.getInterest().getInterestId()
                ).stream())
                .collect(Collectors.toList());

        // 유저의 위치와 추천 장소 위치가 동일하다면 출력
        Optional<Place> matchedPlace = places.stream()
                .filter(place -> LocationUtils.getDistance(lat, lng, place.getLatitude(), place.getLongitude()
                ) <= 100)
                .findFirst();

        // 인증할 곳이 아닌 경우
        if (matchedPlace.isEmpty()) {
            return new CertificationResponse(false, "인증 가능한 장소가 없습니다.");
        }

        // 오늘 이미 인증했는지를 확인하기 위한 db 점검
        Place place = matchedPlace.get();

        boolean alreadyCertified = certificationRepository.existsByUserAndPlaceAndTeam(user, place, team);

        // 이미 인증했는지 확인
        if (alreadyCertified) {
            return new CertificationResponse(false, "이미 인증하셨습니다.");
        }

        else {
            // 인증 기록 저장
            Certification newCertification = new Certification();
            newCertification.setCertifiedDate(LocalDate.now());
            newCertification.setUser(user);
            newCertification.setPlace(place);
            newCertification.setTeam(team);

            certificationRepository.save(newCertification);
            rankService.addStamps(userId, 1);

            return new CertificationResponse(true, "인증 성공! 스탬프가 지급되었습니다.");

        }


    }
}
