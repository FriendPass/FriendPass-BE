package com.likelion.friendpass.domain.rank;

import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_rewards",
        indexes = {
                @Index(name = "ix_user_rewards_total", columnList = "total_stamps")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRewards {

    // PK이자 FK (users.user_id)
    @Id
    @Column(name = "user_id")
    private Long userId;

    // 공유 기본키 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_stamps", nullable = false)
    private int totalStamps;

    @Column(name = "last_certified")
    private LocalDateTime lastCertified;

    /* ===== 도메인 메서드 ===== */
    public void addStamps(int delta) {
        this.totalStamps += delta;
        this.lastCertified = LocalDateTime.now();
    }

    public void certifyNow() {
        this.lastCertified = LocalDateTime.now();
    }
}
