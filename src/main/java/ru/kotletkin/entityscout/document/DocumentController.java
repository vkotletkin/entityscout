package ru.kotletkin.entityscout.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kotletkin.entityscout.document.model.TikaContent;

import java.util.List;

@RestController
@RequestMapping("/api/documents/extract")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentExtractorService documentExtractorService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<TikaContent> extractAutoInfoAboutDocument(@RequestPart("file") MultipartFile file,
                                                          @RequestParam("includeAttachments") boolean includeAttachments) {
        return documentExtractorService.extractDocumentsAuto(file, includeAttachments);
    }

}
