package com.likelion.friendpass.domain.translation;

public interface TranslationService {
    /** 원문(text)을 targetLang으로 번역. source는 자동감지 */
    TranslationResult translate(String text, String targetLang);

    record TranslationResult(String translatedText, String detectedSourceLang) {}
}