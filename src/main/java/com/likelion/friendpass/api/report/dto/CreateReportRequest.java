package com.likelion.friendpass.api.report.dto;

import com.likelion.friendpass.domain.report.ReportReasonCategory;
import com.likelion.friendpass.api.report.validation.ValidReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidReportReason
public record CreateReportRequest(
        @NotNull Long reportedUserId,
        Long chatId,
        Long messageId,
        @NotNull ReportReasonCategory reasonCategory,
        @Size(max = 200) String reasonText
) {}