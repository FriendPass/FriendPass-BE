package com.likelion.friendpass.scheduler;

import com.likelion.friendpass.api.matching.MatchingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
@Component
public class TeamMatchingScheduler {

    private final MatchingService matchingService;

    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    public void schedule() {
        System.out.println("🔁 매칭 API 호출 시작");

        matchingService.createMatchingTeam();

    }
}
