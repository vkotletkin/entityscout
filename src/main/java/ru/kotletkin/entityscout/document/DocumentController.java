package ru.kotletkin.entityscout.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kotletkin.entityscout.document.dto.DocumentInfo;
import ru.kotletkin.entityscout.document.dto.DocumentType;

import java.util.List;

@RestController
@RequestMapping("/api/documents/extract")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<DocumentInfo> extractAutoInfoAboutDocument(@RequestPart("file") MultipartFile file,
                                                           @RequestParam(value = "documentType", defaultValue = "AUTO") DocumentType documentType,
                                                           @RequestParam("includeAttachments") boolean isIncludeAttachments) {
        return documentService.extractDocumentsAuto(file, documentType, isIncludeAttachments);
    }

    @PostMapping(value = "/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> extractAttachmentToZip(@RequestPart("file") MultipartFile file,
                                                         @RequestParam(value = "documentType", defaultValue = "AUTO") DocumentType documentType) {
        byte[] responseByteArray = documentService.extractAttachmentOnZip(file, documentType);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"attachments.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseByteArray);
    }
}
