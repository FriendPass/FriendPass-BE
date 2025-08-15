package com.likelion.friendpass.domain.interest;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "user_interests")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class UserInterest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connect_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "interest_id", nullable = false)
    private Long interestId;

    public static UserInterest of(Long userId, Long interestId) {
        return UserInterest.builder().userId(userId).interestId(interestId).build();
    }
}