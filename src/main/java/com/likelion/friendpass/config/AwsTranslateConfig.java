package com.likelion.friendpass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;

@Configuration
public class AwsTranslateConfig {

    @Value("${aws.region:ap-northeast-2}")
    private String region;

    @Bean
    public TranslateClient translateClient() {
        return TranslateClient.builder()
                .region(Region.of(region))                       // 리전 명시
                .credentialsProvider(DefaultCredentialsProvider.create()) // 자격 기본체인
                .build();
    }
}
