package com.likelion.friendpass.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporter_UserIdAndDedupKey(Long reporterUserId, String dedupKey);

    Page<Report> findByReporter_UserId(Long reporterUserId, Pageable pageable);
}
