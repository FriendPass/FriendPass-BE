package com.likelion.friendpass.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReasonCategory {
    ABUSE("욕설/비하", "Abuse/Insult"),
    SEXUAL("성희롱/부적절한 언행", "Sexual Misconduct"),
    SPAM("스팸/광고", "Spam/Ads"),
    SCAM("사기/금전 요구", "Scam/Money Solicitation"),
    OFFLINE("오프라인 위협/위험 행동", "Offline Threat/Dangerous Behavior"),
    OTHER("기타(직접 작성)", "Other");

    private final String ko;
    private final String en;
}