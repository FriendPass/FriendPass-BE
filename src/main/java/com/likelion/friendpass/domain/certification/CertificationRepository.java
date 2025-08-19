package com.likelion.friendpass.domain.certification;

import com.likelion.friendpass.domain.matching.MatchingTeam;
import com.likelion.friendpass.domain.place.Place;
import com.likelion.friendpass.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findByCertifiedDateAndUserAndPlace(LocalDate certifiedDate, User user, Place place);
    boolean existsByUserAndPlaceAndTeam(User user, Place place, MatchingTeam team);
}
