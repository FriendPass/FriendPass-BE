package com.likelion.friendpass.domain.report;

import com.likelion.friendpass.api.report.dto.CreateReportRequest;
import com.likelion.friendpass.domain.chat.ChatAccessChecker;
import com.likelion.friendpass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ChatAccessChecker chatAccessChecker;

    @Transactional
    public Long create(Long reporterId, CreateReportRequest req) {
        if (reporterId.equals(req.reportedUserId()))
            throw new IllegalArgumentException("자기 자신은 신고할 수 없습니다.");

        if (req.chatId() != null)
            chatAccessChecker.ensureBothMembers(reporterId, req.reportedUserId(), req.chatId());

        String dedup = makeDedupKey(req);

        if (reportRepository.existsByReporter_UserIdAndDedupKey(reporterId, dedup)) {
            throw new IllegalStateException("동일 맥락 신고가 이미 접수되었습니다.(24h)");
        }

        var reporter = userRepository.getReferenceById(reporterId);
        var reported = userRepository.getReferenceById(req.reportedUserId());

        var report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setChatId(req.chatId());
        report.setMessageId(req.messageId());
        report.setReasonCategory(req.reasonCategory());
        report.setReasonText(req.reasonText());
        report.setDedupKey(dedup);

        return reportRepository.save(report).getId();
    }

    private String makeDedupKey(CreateReportRequest req) {
        String date = LocalDate.now(ZoneOffset.UTC).toString();
        String raw = (req.reportedUserId() + ":" +
                String.valueOf(req.messageId()) + ":" +
                String.valueOf(req.chatId()) + ":" +
                req.reasonCategory().name() + ":" + date);
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return raw;
        }
    }
}