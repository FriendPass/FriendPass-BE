package com.likelion.friendpass.domain.place;

import com.likelion.friendpass.domain.interest.InterestTag;
import com.likelion.friendpass.domain.matching.MatchingMember;
import com.likelion.friendpass.domain.matching.MatchingRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByRegionAndInterest(MatchingRegion region, InterestTag interest);
}
