package com.likelion.friendpass.infra.translation;

import com.likelion.friendpass.domain.translation.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;

@Service
@RequiredArgsConstructor
public class AwsTranslationService implements TranslationService {

    private final TranslateClient translateClient;

    @Override
    public TranslationResult translate(String text, String targetLang) {
        if (text == null || text.isBlank()) {
            return new TranslationResult(text, null);
        }
        var req = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode("auto")
                .targetLanguageCode(targetLang)
                .build();
        var res = translateClient.translateText(req);
        return new TranslationResult(res.translatedText(), res.sourceLanguageCode());
    }
}

