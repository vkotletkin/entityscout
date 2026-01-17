package ru.kotletkin.entityscout.document;

import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import ru.kotletkin.entityscout.document.dto.DocumentType;
import ru.kotletkin.entityscout.document.extractor.NoEmbeddedDocumentExtractor;
import ru.kotletkin.entityscout.document.model.TikaContent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentExtractorService {

    @Qualifier("recursiveAutoDetect")
    private final RecursiveParserWrapper autoDetectResursiveParser;

    @Qualifier("recursive822Parser")
    private final RecursiveParserWrapper rfc822RecursiveParser;

    public List<TikaContent> extractDocumentsAuto(MultipartFile file, boolean isIncludeAttachments) {
        try {

            ParseContext parseContext = new ParseContext();

            if (!isIncludeAttachments) {
                parseContext.set(EmbeddedDocumentExtractor.class, new NoEmbeddedDocumentExtractor());
            }

            return processDocument(file.getInputStream(), parseContext);
        } catch (IOException _) {
            throw new RuntimeException();
        }
    }

    private List<TikaContent> processDocument(InputStream inputStream, ParseContext parseContext) {

        Metadata metadata = new Metadata();
        RecursiveParserWrapperHandler handler = createRecursiveParserWrapperHandler();

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            autoDetectResursiveParser.parse(bufferedInputStream, handler, metadata, parseContext);
            List<Metadata> metadataList = handler.getMetadataList();
            return DocumentMapper.toTikaContent(metadataList);
        } catch (IOException | SAXException | TikaException _) {
            throw new RuntimeException();
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
