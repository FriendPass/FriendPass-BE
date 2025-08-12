package com.likelion.friendpass.domain.user;

import com.likelion.friendpass.domain.school.School;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // 사용자 ID

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 학교 이메일

    @Column(nullable = false, length = 255)
    private String password; // 비밀번호

    @Column(nullable = false, length = 50)
    private String nickname; // 닉네임

    @Column(nullable = false, length = 50)
    private String nationality; // 국적

    @Column(name = "is_exchange", nullable = false)
    private Boolean isExchange; // 교환학생 여부

    @Column(nullable = false, length = 30)
    private String language; // 기본 언어

    @Column(name = "profile_image", nullable = false, length = 255)
    private String profileImage; // 프로필 이미지 URL

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일시

    @Column(name = "is_active", nullable = false)
    private Boolean isActive; // 계정 활성 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school; // 소속학교 ID
}