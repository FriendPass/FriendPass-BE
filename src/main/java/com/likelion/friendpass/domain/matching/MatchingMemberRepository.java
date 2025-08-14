package com.likelion.friendpass.domain.matching;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingMemberRepository extends JpaRepository<MatchingMember, Long> {
    List<MatchingMember> findByTeam_teamId(long teamId);
}
