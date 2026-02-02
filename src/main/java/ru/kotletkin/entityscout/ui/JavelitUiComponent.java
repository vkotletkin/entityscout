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
import ru.kotletkin.entityscout.search.dto.SearchSingleDTO;

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
                        Jt.page("/home", this::homePage).title("Home Page").icon("\uD83C\uDFE0"),
                        Jt.page("/language-detector", this::languageDetectorPage).title("Language Detection").icon("\uD83D\uDCAC"),
                        Jt.page("/extract-text", this::textExtractionPage).title("Text Extraction").icon("\uD83D\uDCDD"),
                        Jt.page("/search-lucene", this::luceneSearchPage).title("Text Search").icon("\uD83D\uDD0D"))
                .use();
        page.run();
    }

    private void homePage() {

        Jt.html("<h1 style='text-align: center;'>\uD83C\uDFE0 Home Page</h1>").use();
        Jt.divider().use();

        Jt.html("<h2 style='text-align: center;'>This is the start page of the Entity Scout service</h2>").use();

        Jt.text("The web page is intended to introduce functionality and is not intended for production environments. " +
                "Use the API. An example can be seen on /swagger-ui/index.html the endpoint. The service implements document extraction recursively using Apache Tika, and detects the language using the Optimaize Language Detector. " +
                "Allows you to perform queries using Apache Lucene using MemoryIndex, which provides incredibly high efficiency when searching on a thread.").use();
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

    private void luceneSearchPage() {

        Jt.html("<h1 style='text-align: center;'>\uD83D\uDD0D Text Search (Apache Lucene Engine)</h1>").use();
        Jt.divider().use();

        var form = Jt.form().use();
        String text = Jt.textArea("Entire your text").use(form);
        String query = Jt.textArea("Entire your Lucene query").use(form);

        if (Jt.formSubmitButton("Execute query").use(form) && !text.isBlank() && !query.isBlank()) {
            try {
                SearchSingleDTO searchSingleDTO = searchService.searchBySingleRequest(text, query);
                Jt.success("Query execution - SUCCESS!").use();
                Jt.text("Result: " + searchSingleDTO.result()).use();
                Jt.text("Score: " + searchSingleDTO.score()).use();
            } catch (Exception e) {
                Jt.divider().use();
                Jt.error("Check the correctness of the request").use();
            }
        }
    }

}
