package com.likelion.friendpass.domain.matching;

import com.likelion.friendpass.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {
    Optional<MatchingRequest> findByUserAndStatus(User user, MatchingStatus status);
    List<MatchingRequest> findByTeam_TeamId(Long teamId);
    List<MatchingRequest> findByStatus(MatchingStatus status);
}
