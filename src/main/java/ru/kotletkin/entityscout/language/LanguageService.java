package ru.kotletkin.entityscout.language;

import lombok.RequiredArgsConstructor;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.springframework.stereotype.Service;
import ru.kotletkin.entityscout.language.dto.LanguageDetectionDTO;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDetector languageDetector;

    public synchronized LanguageDetectionDTO detectLanguage(String text) {
        LanguageResult result = languageDetector.detect(text);
        String language = !result.isUnknown() ? result.getLanguage() : "unknown";
        return new LanguageDetectionDTO(language, result);
    }
}
