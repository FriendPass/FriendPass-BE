package com.likelion.friendpass.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndIsActiveFalse(String email);
    Optional<User> findByEmailAndIsActiveTrue(String email);

    @EntityGraph(attributePaths = {"nationality", "school"})
    Optional<User> findByUserIdAndIsActiveTrue(Long userId);
}