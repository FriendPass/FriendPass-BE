package com.likelion.friendpass.api.report;

import com.likelion.friendpass.api.report.dto.CreateReportRequest;
import com.likelion.friendpass.domain.report.ReportReasonCategory;
import com.likelion.friendpass.domain.report.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/reasons")
    public ResponseEntity<List<Map<String, String>>> reasons() {
        var out = Arrays.stream(ReportReasonCategory.values())
                .map(e -> Map.of(
                        "code", e.name(),
                        "labelKo", e.getKo(),
                        "labelEn", e.getEn()))
                .toList();
        return ResponseEntity.ok(out);
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @AuthenticationPrincipal Long reporterId,
            @Valid @RequestBody CreateReportRequest req
    ) {
        Long id = reportService.create(reporterId, req);
        return ResponseEntity.status(201).body(Map.of("reportId", id));
    }
}
