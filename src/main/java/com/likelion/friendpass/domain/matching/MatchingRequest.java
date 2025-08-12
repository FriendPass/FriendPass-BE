package com.likelion.friendpass.domain.matching;

import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "matching_requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="request_id")
    private Long requestId;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private MatchingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="region", nullable=false)
    private MatchingRegion region;

    @Column(name="requested_at", nullable=false)
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable=false)
    private MatchingTeam matchingTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = MatchingStatus.대기중;
        }

        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }
}
