package com.likelion.friendpass.api.rank;

import com.likelion.friendpass.api.rank.dto.LeaderboardItemDto;
import com.likelion.friendpass.api.rank.dto.MyRankDto;
import com.likelion.friendpass.api.rank.dto.RankEntryDto;
import com.likelion.friendpass.domain.rank.UserRewards;
import com.likelion.friendpass.domain.rank.UserRewardsRepository;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRewardsRepository userRewardsRepository;
    private final UserRepository userRepository;

    /** 프론트 전용: 전체 배열(JSON array) 반환 */
    @Transactional(readOnly = true)
    public List<LeaderboardItemDto> getLeaderboardItems(Long currentUserId) {
        List<RankEntryDto> rows = userRewardsRepository.findLeaderboardAll(); // 전체 조회

        List<LeaderboardItemDto> items = new ArrayList<>(rows.size());
        long rank = 1L; // 1-base

        for (RankEntryDto row : rows) {
            boolean isMe = currentUserId != null
                    && row.getUserId() != null
                    && row.getUserId().equals(currentUserId);

            String id = isMe ? "me01" : ("u" + row.getUserId());

            items.add(LeaderboardItemDto.builder()
                    .id(id)
                    .rank(rank)
                    .name(row.getNickname())
                    .stamp(row.getTotalStamps())
                    .isMe(isMe)
                    .build());
            rank++;
        }
        return items;
    }

    /** 내 랭킹 + 내 스탬프 */
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

    /** 스탬프 적립/차감 (없으면 생성 후 증가) */
    @Transactional
    public void addStamps(Long userId, int delta) {
        if (delta == 0) return;
        userRewardsRepository.incrementOrCreate(userId, delta);
    }

    /** ‘인증’ 같은 +1 케이스 */
    @Transactional
    public void certifyNow(Long userId) {
        addStamps(userId, 1);
    }
}
