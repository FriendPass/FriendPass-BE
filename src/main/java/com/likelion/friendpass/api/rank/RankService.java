package com.likelion.friendpass.api.rank;

import com.likelion.friendpass.api.rank.dto.*;
import com.likelion.friendpass.domain.rank.UserRewards;
import com.likelion.friendpass.domain.rank.UserRewardsRepository;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRewardsRepository userRewardsRepository;
    private final UserRepository userRepository;

    /** 전체 랭킹 페이지 조회 (닉네임/프로필 포함) */
    @Transactional(readOnly = true)
    public Page<RankEntryDto> getLeaderboard(Pageable pageable) {
        Page<RankEntryDto> page = userRewardsRepository.findLeaderboardView(pageable);

        long startRank = pageable.getOffset() + 1; // 1-base
        // 정렬 기준이 totalStamps desc, userId asc 로 고정되어 있으므로
        // 시작 순위 + 인덱스로 순위 계산 가능 (동점자 규칙이 'tie-break'이지 '공동순위'가 아님)
        int i = 0;
        for (RankEntryDto dto : page.getContent()) {
            dto.setRank(startRank + i);
            i++;
        }
        return page;
    }

    /** 내 랭킹 + 내 스탬프 조회 */
    @Transactional(readOnly = true)
    public MyRankDto getMyRank(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserRewards ur = userRewardsRepository.findById(userId)
                .orElseGet(() -> UserRewards.builder()
                        .userId(userId).user(user).totalStamps(0).lastCertified(null).build());

        long rank = userRewardsRepository.rankOf(userId);
        return MyRankDto.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .totalStamps(ur.getTotalStamps())
                .rank(rank)
                .build();
    }

    /** 스탬프 적립/차감(누적 랭킹 시스템의 핵심) */
    @Transactional
    public void addStamps(Long userId, int delta) {
        if (delta == 0) return;

        // DB에서 유저 row가 없으면 생성, 있으면 delta만 증가
        int updated = userRewardsRepository.incrementOrCreate(userId, delta);

        // updated는 영향을 받은 row 수
        // UPSERT라서 항상 1 이상일 것임
    }


    /** ‘인증’ 버튼 등에서 오늘 인증만 기록하고 싶을 때 */
    @Transactional
    public void certifyNow(Long userId) {
        // 인증 시 1스탬프 적립이라고 가정(요구에 맞게 delta 조정)
        addStamps(userId, 1);
    }
}

