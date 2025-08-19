package com.likelion.friendpass.domain.interest;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    @Modifying
    @Query("delete from UserInterest ui where ui.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("""
           select it.name
           from UserInterest ui
           join InterestTag it on it.interestId = ui.interestId
           where ui.userId = :userId
           """)
    List<String> findNamesByUserId(@Param("userId") Long userId);

    @Query("""
            select it.interestId
            from UserInterest ui
            join InterestTag it on it.interestId = ui.interestId
            where ui.userId = :userId
            """)
    List<Long> findInterestIdsByUserId(@Param("userId") Long userId);

}