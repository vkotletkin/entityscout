package ru.kotletkin.entityscout.language;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotletkin.entityscout.common.dto.BaseRequest;
import ru.kotletkin.entityscout.language.dto.LanguageDetectionDTO;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;
    @PostMapping
    public LanguageDetectionDTO detectLanguage(@Valid @RequestBody BaseRequest request) {
        return languageService.detectLanguage(request.text());
    }
}
