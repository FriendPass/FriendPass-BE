package com.likelion.friendpass.api.matching;

import com.likelion.friendpass.api.matching.dto.MatchingCompleteResponse;
import com.likelion.friendpass.api.matching.dto.MatchingRequestCreate;
import com.likelion.friendpass.api.matching.dto.MatchingStatusResponse;
import com.likelion.friendpass.domain.matching.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
public class MatchingController {

    private final MatchingService matchingService;

    // 매칭 상태 확인 (신청 전 / 대기중)
    @GetMapping("/status")
    public MatchingStatusResponse getStatus(@RequestParam Long userId) {
        return matchingService.getWaitingStatus(userId);
    }

    // 매칭 신청하기
    @PostMapping("/request")
    public void createMatchingRequest(@RequestBody MatchingRequestCreate dto) {
        matchingService.createMatchingRequest(dto);
    }

    // 매칭하기
    @PostMapping("/create-team")
    public MatchingTeam createMatchingTeam(@RequestParam MatchingRegion region) {
        return matchingService.createMatchingTeam(region);
    }

    // 매칭 완료 화면
    @GetMapping("/complete")
    public MatchingCompleteResponse getComplete(@RequestParam Long teamId) {
        return matchingService.getMatchingComplete(teamId);
    }


}
