package com.likelion.friendpass.api.matching;

import com.likelion.friendpass.api.matching.dto.MatchingCompleteResponse;
import com.likelion.friendpass.api.matching.dto.MatchingRequestCreate;
import com.likelion.friendpass.api.matching.dto.MatchingStatusResponse;
import com.likelion.friendpass.domain.matching.*;
import com.likelion.friendpass.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
public class MatchingController {

    private final MatchingService matchingService;

    // 매칭 상태 확인 (신청 전 / 대기중)
    @GetMapping("/status")
    public MatchingStatusResponse getStatus() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return matchingService.getWaitingStatus(userId);
    }

    // 매칭 신청하기
    @PostMapping("/request")
    public ResponseEntity<Void> createMatchingRequest(
            @RequestBody MatchingRequestCreate requestDto,
            @AuthenticationPrincipal User user
    ) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        matchingService.createMatchingRequest(userId, requestDto);
        return ResponseEntity.ok().build(); // 200 OK, 바디 없음
    }


    // 매칭하기
    @PostMapping("/create-team")
    public List<MatchingCompleteResponse> createMatchingTeam() {
        return matchingService.createMatchingTeam();
    }

    // 매칭 완료 화면
    @GetMapping("/complete")
    public MatchingCompleteResponse getComplete() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return matchingService.getMatchingComplete(userId);
    }

    // 매칭 끝내기
    @PostMapping("/exit")
    public ResponseEntity<Void> exitMatch(@RequestBody MatchingRequest requestDto) {
        matchingService.exitMatching(requestDto.getTeam().getTeamId());
        return ResponseEntity.ok().build();
    }



}
