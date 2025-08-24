package com.likelion.friendpass.api.rank;

import com.likelion.friendpass.api.rank.dto.LeaderboardItemDto;
import com.likelion.friendpass.api.rank.dto.MyRankDto;
import com.likelion.friendpass.api.rank.dto.StampUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    /** 전체 랭킹 조회: 리스트 형식 반환 */
    @GetMapping("/leaderboard")
    public List<LeaderboardItemDto> leaderboard(@AuthenticationPrincipal Long userId) { // ★ 수정: page/size 제거
        return rankService.getLeaderboardItems(userId); // ★ 수정: Pageable 제거
    }

    /** 내 랭킹/스탬프 */
    @GetMapping("/me")
    public MyRankDto myRank(@AuthenticationPrincipal Long userId) {
        return rankService.getMyRank(userId);
    }

    /** 스탬프 증감(관리/테스트용 또는 특정 액션 훅에서 호출) */
    @PostMapping("/stamps")
    public void addStamps(@AuthenticationPrincipal Long userId, @RequestBody StampUpdateReq req) {
        rankService.addStamps(userId, req.getDelta());
    }

    // ★ 삭제: page/size 파라미터와 관련된 코드 전부
}
