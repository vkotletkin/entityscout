package ru.kotletkin.entityscout.ui;

import io.javelit.core.Jt;
import io.javelit.core.JtUploadedFile;
import io.javelit.core.Server;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kotletkin.entityscout.document.DocumentService;
import ru.kotletkin.entityscout.document.dto.DocumentInfo;
import ru.kotletkin.entityscout.document.dto.DocumentType;
import ru.kotletkin.entityscout.language.LanguageService;
import ru.kotletkin.entityscout.language.dto.LanguageDetectionDTO;
import ru.kotletkin.entityscout.search.SearchService;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JavelitUiComponent {

    private final LanguageService languageService;
    private final DocumentService documentService;
    private final SearchService searchService;

    @PostConstruct
    public void startJavelitServer() {
        var server = Server.builder(this::app, 8888).build();
        server.start();
    }

    private void app() {

        var page = Jt.navigation(
                        Jt.page("/home", this::homePage).title("Home Page"),
                        Jt.page("/language-detector", this::languageDetectorPage).title("Language Detection").icon("\uD83D\uDCAC"),
                        Jt.page("/extract-text", this::textExtractionPage).title("Text Extraction").icon("\uD83D\uDCDD"))
                .use();
        page.run();
    }

    private void homePage() {
        Jt.title("This is home page").use();
    }

    private void languageDetectorPage() {

        Jt.html("<h1 style='text-align: center;'>\uD83D\uDCAC Language Detection</h1>").use();
        Jt.divider().use();

        var form = Jt.form().use();
        String text = Jt.textArea("Entire your text").use(form);

        if (Jt.formSubmitButton("Detect language").use(form) && !text.isBlank()) {
            LanguageDetectionDTO languageDetectionDTO = languageService.detectLanguage(text);
            Jt.success("Language detection - SUCCESS!").use();
            Jt.text("Language: " + languageDetectionDTO.language()).use();
            Jt.text("Details: [" + languageDetectionDTO.details() + "]").use();
        }
    }

    private void textExtractionPage() {

        Jt.html("<h1 style='text-align: center;'>\uD83D\uDCDD Text Extraction</h1>").use();

        Jt.divider().use();
        Jt.info("Do not use large files in the Web version, send ultra-large files via API. The Web version is used exclusively for tests").use();
        Jt.divider().use();

        var uploadedFiles = Jt.fileUploader("Choose a file").use();

        if (!uploadedFiles.isEmpty()) {
            JtUploadedFile file = uploadedFiles.getFirst();
            String filename = file.filename();
            List<DocumentInfo> documentInfos = documentService.extractDocumentsAuto(
                    new ByteArrayInputStream(file.content()), filename, DocumentType.AUTO, true);

            Jt.success("Text extraction - SUCCESS!").use();
            Jt.divider().use();

            if (!documentInfos.isEmpty()) {
                for (DocumentInfo documentInfo : documentInfos) {
                    var expander = Jt.expander(documentInfo.resourceName()).use();
                    Jt.text(documentInfo.text()).use(expander);
                }
            }
        }

    }

}
