package com.likelion.friendpass.scheduler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

public class TeamMatchingScheduler {

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @Scheduled(cron = "0 */30 * * * *")
    public void schedule() {
        System.out.println("🔁 매칭 API 호출 시작");

        webClient.post()
                .uri("matching/create-team")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("매칭 호출 실패" + error.getMessage()))
                .doOnSuccess(response -> System.out.println("매칭 호출 성공"))
                .subscribe();

    }
}
