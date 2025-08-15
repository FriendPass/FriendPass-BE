package com.likelion.friendpass.domain.matching;

import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="matching_members")
@AllArgsConstructor
@NoArgsConstructor
public class MatchingMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="matching_member_id")
    private Long matchingMemberId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private MatchingTeam team;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
