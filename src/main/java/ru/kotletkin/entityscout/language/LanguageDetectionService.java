package ru.kotletkin.entityscout.language;

import lombok.RequiredArgsConstructor;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageDetectionService {

    private final LanguageDetector languageDetector;

    public synchronized String detectLanguage(String text) {
        LanguageResult result = languageDetector.detect(text);
        return !result.isUnknown() ? result.getLanguage() : "unknown";
    }
}
