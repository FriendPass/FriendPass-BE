package com.likelion.friendpass.domain.matching;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "matching_teams")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="team_id")
    private Long teamId;

    @Column(name="matched_at", nullable=false)
    private LocalDateTime matchedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="matched_region", nullable = false)
    private MatchingRegion matchedRegion;

}
