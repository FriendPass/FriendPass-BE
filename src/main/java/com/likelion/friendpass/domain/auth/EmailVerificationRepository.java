package com.likelion.friendpass.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailOrderByExpiresAtDesc(String email);
    boolean existsByEmailAndVerifiedTrue(String email);
}