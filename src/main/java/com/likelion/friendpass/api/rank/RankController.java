package com.likelion.friendpass.api.rank;

import com.likelion.friendpass.api.rank.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    /** 전체 랭킹 조회 (페이지네이션) */
    @GetMapping("/leaderboard")
    public Page<RankEntryDto> leaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return rankService.getLeaderboard(pageable);
    }

    /** 내 랭킹/스탬프 */
    @GetMapping("/me")
    public MyRankDto myRank(@AuthenticationPrincipal Long userId) {
        return rankService.getMyRank(userId);
    }

    /** 스탬프 증감(관리/테스트용 또는 특정 액션 훅에서 호출) */
    @PostMapping("/stamps")
    public void addStamps(
            @AuthenticationPrincipal Long userId,
            @RequestBody StampUpdateReq req
    ) {
        rankService.addStamps(userId, req.getDelta());
    }

}

