package com.likelion.friendpass.domain.place;

import com.likelion.friendpass.domain.matching.MatchingMember;
import com.likelion.friendpass.domain.matching.MatchingRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    // 나중에 관심사 들어오면 수정
    // List<Place> findByRegionAndCategory_CategoryId(MatchingRegion region, Category categoryId);
}
