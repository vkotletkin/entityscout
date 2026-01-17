package ru.kotletkin.entityscout.document;

import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import ru.kotletkin.entityscout.document.dto.DocumentInfo;
import ru.kotletkin.entityscout.document.dto.DocumentType;
import ru.kotletkin.entityscout.document.extractor.NoEmbeddedDocumentExtractor;
import ru.kotletkin.entityscout.document.model.TikaContent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentExtractorService {

    @Qualifier("recursiveAutoDetect")
    private final RecursiveParserWrapper autoDetectResursiveParser;

    public List<TikaContent> extractDocumentsAuto(MultipartFile file, DocumentType documentType, boolean isIncludeAttachments,
                                                  boolean isCleanText) {
        try {

            ParseContext parseContext = new ParseContext();
            Metadata metadata = new Metadata();

            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getOriginalFilename());

            switch (documentType) {
                case AUTO -> {
                }
                case RFC822 ->
                        metadata.set(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE, documentType.getContentTypeOverride());
            }

            if (!isIncludeAttachments) {
                parseContext.set(EmbeddedDocumentExtractor.class, new NoEmbeddedDocumentExtractor());
            }

            List<TikaContent> tikaContents = processDocument(file.getInputStream(), metadata, parseContext);
            return tikaContents;
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

    private void postProcessingDocument(List<TikaContent> tikaContents) {
        List<DocumentInfo> documentInfos = new ArrayList<>();
        for (TikaContent tikaContent : tikaContents) {

        }
    }

    private RecursiveParserWrapperHandler createRecursiveParserWrapperHandler() {
        return new RecursiveParserWrapperHandler(
                new BasicContentHandlerFactory(
                        BasicContentHandlerFactory.HANDLER_TYPE.TEXT,
                        -1
                )
        );
    }
}
