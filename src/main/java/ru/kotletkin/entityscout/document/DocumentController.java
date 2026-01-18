package ru.kotletkin.entityscout.document;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kotletkin.entityscout.document.dto.DocumentInfo;
import ru.kotletkin.entityscout.document.dto.DocumentType;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/documents/extract")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<DocumentInfo> extractAutoInfoAboutDocument(@RequestPart("file") MultipartFile file,
                                                           @RequestParam(value = "documentType", defaultValue = "AUTO") DocumentType documentType,
                                                           @RequestParam(value = "includeAttachments", defaultValue = "true") boolean isIncludeAttachments) {
        return documentService.extractDocumentsAuto(file, documentType, isIncludeAttachments);
    }

    @PostMapping(value = "/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> extractAttachmentToZip(@RequestPart("file") MultipartFile file,
                                                         @RequestParam(value = "documentType", defaultValue = "AUTO") DocumentType documentType,
                                                         @RequestParam(value = "maximumDepth", defaultValue = "10") @Min(1) int maximumDepth) {
        byte[] responseByteArray = documentService.extractAttachmentOnZip(file, documentType, maximumDepth);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"attachments.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseByteArray);
    }
}
