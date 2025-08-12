package com.likelion.friendpass.entity.school;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private Long schoolId; // 학교 ID

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName; // 학교 이름

    @Column(nullable = false, length = 100)
    private String domain; // 이메일 도메인
}