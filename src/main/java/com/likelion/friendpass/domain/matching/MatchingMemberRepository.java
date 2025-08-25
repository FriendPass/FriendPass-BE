package com.likelion.friendpass.domain.matching;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingMemberRepository extends JpaRepository<MatchingMember, Long> {
    List<MatchingMember> findByTeam(MatchingTeam teamId);
    boolean existsByTeam_TeamIdAndUser_UserId(Long teamId, Long userId);
    List<MatchingMember> findByTeam_TeamId(Long teamId);
}
