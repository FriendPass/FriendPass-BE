package com.likelion.friendpass.domain.report;

import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter @Setter
@Entity
@Table(name = "report", uniqueConstraints = {
        @UniqueConstraint(name="uk_report_dedup", columnNames = {"reporter_id","dedup_key"})
})
public class Report {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY) @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = LAZY) @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "message_id")
    private Long messageId;

    @Enumerated(STRING)
    @Column(name = "reason_category", nullable = false, length = 32)
    private ReportReasonCategory reasonCategory;

    @Column(name = "reason_text", length = 200)
    private String reasonText;

    @Enumerated(STRING)
    @Column(nullable = false, length = 16)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "dedup_key", nullable = false, length = 64)
    private String dedupKey;

    @Column(nullable = false)
    private boolean withdrawn = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportEvidence> evidences = new ArrayList<>();
}