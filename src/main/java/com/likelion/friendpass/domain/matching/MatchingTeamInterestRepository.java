package com.likelion.friendpass.domain.matching;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingTeamInterestRepository extends JpaRepository<MatchingTeamInterest, Long> {
    List<MatchingTeamInterest> findByTeam_TeamId(Long teamId);
}
