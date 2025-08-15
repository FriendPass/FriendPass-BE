package com.likelion.friendpass.domain.interest;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "interest_tags")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class InterestTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long id;

    @Column(name = "interest_name", nullable = false, unique = true)
    private String name;
}