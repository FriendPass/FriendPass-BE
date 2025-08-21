package com.likelion.friendpass.domain.rank;

import com.likelion.friendpass.api.rank.dto.RankEntryDto;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface UserRewardsRepository extends JpaRepository<UserRewards, Long> {

    // (기존) 리더보드 정렬
    @Query("select ur from UserRewards ur order by ur.totalStamps desc, ur.userId asc")
    Page<UserRewards> findLeaderboard(Pageable pageable);

    // (기존) 특정 유저의 랭킹 (동점자일 때 userId 작은 사람이 앞)
    @Query("""
        select count(ur) + 1
        from UserRewards ur
        where (ur.totalStamps > (select x.totalStamps from UserRewards x where x.userId = :userId))
           or (ur.totalStamps = (select x.totalStamps from UserRewards x where x.userId = :userId)
               and ur.userId < :userId)
        """)
    long rankOf(@Param("userId") Long userId);

    // 유저와 함께 조회(닉네임/프로필 포함). 페이지 리더보드용
    @Query("""
        select new com.likelion.friendpass.api.rank.dto.RankEntryDto(
            ur.userId, u.nickname, u.profileImage, ur.totalStamps, 0L
        )
        from UserRewards ur
        join ur.user u
        order by ur.totalStamps desc, ur.userId asc
        """)
    Page<RankEntryDto> findLeaderboardView(Pageable pageable);

    // 원자적 증가 (경합 방지). 성공 행 수 반환
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserRewards ur set ur.totalStamps = ur.totalStamps + :delta, ur.lastCertified = CURRENT_TIMESTAMP where ur.userId = :userId")
    int incrementStamps(@Param("userId") Long userId, @Param("delta") int delta);
}
