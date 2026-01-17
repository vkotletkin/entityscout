package ru.kotletkin.entityscout.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kotletkin.entityscout.document.dto.DocumentType;
import ru.kotletkin.entityscout.document.model.TikaContent;

import java.util.List;

@RestController
@RequestMapping("/api/documents/extract")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentExtractorService documentExtractorService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<TikaContent> extractAutoInfoAboutDocument(@RequestPart("file") MultipartFile file,
                                                          @RequestParam(value = "documentType", defaultValue = "AUTO") DocumentType documentType,
                                                          @RequestParam("includeAttachments") boolean isIncludeAttachments,
                                                          @RequestParam("clearText") boolean isCleanText) {
        return documentExtractorService.extractDocumentsAuto(file, documentType, isIncludeAttachments, isCleanText);
    }
}
