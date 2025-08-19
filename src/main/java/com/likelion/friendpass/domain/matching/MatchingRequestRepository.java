package com.likelion.friendpass.domain.matching;

import com.likelion.friendpass.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {
    List<MatchingRequest> findByTeam(MatchingTeam team);
    List<MatchingRequest> findByRegionAndStatus(MatchingRegion region, MatchingStatus status);
    Optional<MatchingRequest> findByUserAndStatus(User user, MatchingStatus status);

    List<MatchingRequest> findByStatus(MatchingStatus status);
}
