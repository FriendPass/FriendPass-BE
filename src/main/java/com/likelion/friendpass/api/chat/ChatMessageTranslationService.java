package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.ChatMessageTranslatedDto;
import com.likelion.friendpass.domain.chat.ChatMessage;
import com.likelion.friendpass.domain.chat.ChatMessageRepository;
import com.likelion.friendpass.domain.chat.ChatMessageTranslation;
import com.likelion.friendpass.domain.chat.ChatMessageTranslationRepository;
import com.likelion.friendpass.domain.translation.TranslationService;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageTranslationService {

    private final ChatMessageRepository msgRepo;
    private final ChatMessageTranslationRepository msgTrRepo;
    private final UserRepository userRepo;
    private final TranslationService translationService;

    @Transactional
    public ChatMessageTranslatedDto translateForUser(Long messageId, Long userId, String targetLangParam) {
        ChatMessage msg = msgRepo.findById(messageId).orElseThrow();

        // 1) 타깃 언어 결정: 요청 파라미터 > 유저 설정 > 기본값(en)
        String targetLang = normalizeLang(
                (targetLangParam != null && !targetLangParam.isBlank())
                        ? targetLangParam
                        : userRepo.findById(userId)
                        .map(this::getUserPreferredLang) // User 엔티티의 언어 필드에 맞춰 수정
                        .orElse("en")
        );

        // 2) 캐시 조회
        var cached = msgTrRepo.findByMessageAndTargetLang(msg, targetLang);
        if (cached.isPresent()) {
            var tr = cached.get();
            return ChatMessageTranslatedDto.of(msg, tr.getTranslatedText(), tr.getTargetLang(), tr.getSourceLang());
        }

        // 3) 번역 호출
        var res = translationService.translate(msg.getText(), targetLang);

        // 4) 캐시 저장
        var saved = msgTrRepo.save(ChatMessageTranslation.builder()
                .message(msg)
                .targetLang(targetLang)
                .translatedText(res.translatedText())
                .sourceLang(res.detectedSourceLang())
                .build());

        return ChatMessageTranslatedDto.of(msg, saved.getTranslatedText(), saved.getTargetLang(), saved.getSourceLang());
    }

    // 유저 엔티티의 언어 게터명에 맞게 수정 (예: getLanguage(), getPreferredLanguage() 등)
    private String getUserPreferredLang(User u) {
        // 예시: u.getLanguage()가 "ko", "en" 같은 코드라고 가정
        String lang = u.getLanguage();
        return (lang == null || lang.isBlank()) ? "en" : lang;
    }

    // 간단 정규화 (소문자, 공백 제거) — 필요시 "zh-CN/zh-TW" 등 추가 매핑
    private String normalizeLang(String lang) {
        return lang.trim().toLowerCase();
    }
}
