package com.likelion.friendpass.domain.matching;

import com.likelion.friendpass.domain.interest.InterestTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Matching_team_interests")
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MatchingTeamInterest {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long representativeId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private MatchingTeam team;

    @ManyToOne
    @JoinColumn(name="interest_id", nullable=false)
    private InterestTag interest;
}
