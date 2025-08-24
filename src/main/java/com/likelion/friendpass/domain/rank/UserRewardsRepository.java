package com.likelion.friendpass.domain.rank;

import com.likelion.friendpass.api.rank.dto.RankEntryDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRewardsRepository extends JpaRepository<UserRewards, Long> {

    // (기존) 특정 유저의 랭킹 (동점자일 때 userId 작은 사람이 앞)
    @Query("""
        select count(ur) + 1
        from UserRewards ur
        where (ur.totalStamps > (select x.totalStamps from UserRewards x where x.userId = :userId))
           or (ur.totalStamps = (select x.totalStamps from UserRewards x where x.userId = :userId)
              and ur.userId < :userId)
        """)
    long rankOf(@Param("userId") Long userId);

    // ★ 추가: 전체 리더보드 조회 (페이지네이션 제거)
    @Query("""
        select new com.likelion.friendpass.api.rank.dto.RankEntryDto(
            ur.userId, u.nickname, u.profileImage, ur.totalStamps, 0L
        )
        from UserRewards ur
        join ur.user u
        order by ur.totalStamps desc, ur.userId asc
        """)
    List<RankEntryDto> findLeaderboardAll();

    // 원자적 증가 (경합 방지). 성공 행 수 반환
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserRewards ur set ur.totalStamps = ur.totalStamps + :delta, ur.lastCertified = CURRENT_TIMESTAMP where ur.userId = :userId")
    int incrementStamps(@Param("userId") Long userId, @Param("delta") int delta);

    @Modifying
    @Query(value = """
        INSERT INTO user_rewards (user_id, total_stamps, last_certified)
        VALUES (:userId, :delta, NOW())
        ON DUPLICATE KEY UPDATE total_stamps = total_stamps + :delta, last_certified = NOW()
        """, nativeQuery = true)
    int incrementOrCreate(@Param("userId") Long userId, @Param("delta") int delta);
}
