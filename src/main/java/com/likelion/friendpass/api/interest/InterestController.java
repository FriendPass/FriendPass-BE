package com.likelion.friendpass.api.interest;

import com.likelion.friendpass.api.user.dto.InterestTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/interests")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;

    @GetMapping
    public ResponseEntity<List<InterestTagResponse>> getAllInterests() {
        return ResponseEntity.ok(interestService.getAll());
    }
}