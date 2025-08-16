package com.likelion.friendpass.domain.certification;

import com.likelion.friendpass.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findByCertifiedDateAndUserAndPlace(LocalDate certificatedDate, User user, Place place);
}
