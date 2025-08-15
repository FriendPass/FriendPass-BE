package com.likelion.friendpass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.likelion.friendpass")
public class FriendPassApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendPassApplication.class, args);
    }

}
