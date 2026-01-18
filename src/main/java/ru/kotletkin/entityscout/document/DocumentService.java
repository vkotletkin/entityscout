package ru.kotletkin.entityscout.document;

import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import ru.kotletkin.entityscout.common.util.TextUtils;
import ru.kotletkin.entityscout.common.util.ZipArchiveBuilder;
import ru.kotletkin.entityscout.document.dto.DocumentInfo;
import ru.kotletkin.entityscout.document.dto.DocumentType;
import ru.kotletkin.entityscout.document.extractor.AttachmentCollectorExtractor;
import ru.kotletkin.entityscout.document.extractor.NoEmbeddedDocumentExtractor;
import ru.kotletkin.entityscout.document.model.TikaContent;
import ru.kotletkin.entityscout.language.LanguageDetectionService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {

    @Qualifier("recursiveAutoDetect")
    private final RecursiveParserWrapper autoDetectResursiveParser;

    @Qualifier("contentHandlerFactoryText")
    private final BasicContentHandlerFactory basicContentHandlerFactoryText;

    @Qualifier("contentHandlerFactoryIgnore")
    private final BasicContentHandlerFactory basicContentHandlerFactoryIgnore;

    private final AutoDetectParser autoDetectParser;
    private final LanguageDetectionService languageDetectionService;

    public byte[] extractAttachmentOnZip(MultipartFile file, DocumentType documentType) {

        Map<String, byte[]> attachments = new HashMap<>();
        ParseContext parseContext = new ParseContext();
        Metadata metadata = new Metadata();
        ContentHandler handler = basicContentHandlerFactoryIgnore.getNewContentHandler();

        parseContext.set(EmbeddedDocumentExtractor.class, new AttachmentCollectorExtractor(attachments));
        processingMetadataOnType(metadata, documentType);

        try (InputStream is = file.getInputStream()) {
            autoDetectParser.parse(is, handler, metadata, parseContext);
            // todo: check empty attachments
            return ZipArchiveBuilder.createZipFromAttachments(attachments);
        } catch (IOException | TikaException | SAXException _) {
            throw new RuntimeException();
        }
    }

    public List<DocumentInfo> extractDocumentsAuto(MultipartFile file, DocumentType documentType, boolean isIncludeAttachments) {
        try {
            ParseContext parseContext = new ParseContext();
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getOriginalFilename());
            processingMetadataOnType(metadata, documentType);

            if (!isIncludeAttachments) {
                parseContext.set(EmbeddedDocumentExtractor.class, new NoEmbeddedDocumentExtractor());
            }

            List<TikaContent> tikaContents = processDocument(file.getInputStream(), metadata, parseContext);
            return postProcessingDocument(tikaContents);
        } catch (IOException _) {
            throw new RuntimeException();
        }
    }

    private List<TikaContent> processDocument(InputStream inputStream, Metadata metadata, ParseContext parseContext) {

        RecursiveParserWrapperHandler handler = createRecursiveParserWrapperHandler();

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            autoDetectResursiveParser.parse(bufferedInputStream, handler, metadata, parseContext);
            List<Metadata> metadataList = handler.getMetadataList();
            return DocumentMapper.toTikaContent(metadataList);
        } catch (IOException | SAXException | TikaException _) {
            throw new RuntimeException();
        }
    }

    private List<DocumentInfo> postProcessingDocument(List<TikaContent> tikaContents) {
        List<DocumentInfo> documentInfos = new ArrayList<>();
        for (TikaContent tikaContent : tikaContents) {
            String rawText = tikaContent.text();
            String cleanText = TextUtils.clean(rawText);
            String language = languageDetectionService.detectLanguage(cleanText);
            DocumentInfo documentInfo = new DocumentInfo(tikaContent.resourceName(),
                    language,
                    tikaContent.title(),
                    tikaContent.contentType(),
                    cleanText,
                    tikaContent.isEncrypted(),
                    tikaContent.metadata());
            documentInfos.add(documentInfo);
        }
        return documentInfos;
    }

    private void processingMetadataOnType(Metadata metadata, DocumentType documentType) {
        switch (documentType) {
            case AUTO -> {
            }
            case RFC822 ->
                    metadata.set(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE, documentType.getContentTypeOverride());
        }
    }

    private RecursiveParserWrapperHandler createRecursiveParserWrapperHandler() {
        return new RecursiveParserWrapperHandler(basicContentHandlerFactoryText);
    }
}
