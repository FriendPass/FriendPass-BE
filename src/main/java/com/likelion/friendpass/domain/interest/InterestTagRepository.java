package com.likelion.friendpass.domain.interest;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {
    List<InterestTag> findAllByOrderByInterestIdAsc();
    long countByInterestIdIn(List<Long> ids);
}
