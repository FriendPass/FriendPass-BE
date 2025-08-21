package com.likelion.friendpass.api.report.validation;

import com.likelion.friendpass.api.report.dto.CreateReportRequest;
import com.likelion.friendpass.domain.report.ReportReasonCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidReportReasonValidator implements ConstraintValidator<ValidReportReason, CreateReportRequest> {
    @Override
    public boolean isValid(CreateReportRequest req, ConstraintValidatorContext ctx) {
        if (req == null) return true;
        if (req.reasonCategory() != ReportReasonCategory.OTHER) return true;
        String t = req.reasonText();
        return t != null && t.strip().length() >= 10 && t.strip().length() <= 200;
    }
}