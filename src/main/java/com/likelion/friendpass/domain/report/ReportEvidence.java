package com.likelion.friendpass.domain.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter @Setter
@Entity
@Table(name = "report_evidence")
public class ReportEvidence {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(length = 255)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}