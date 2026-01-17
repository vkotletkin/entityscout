package ru.kotletkin.entityscout.document.extractor;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

public class NoEmbeddedDocumentExtractor implements EmbeddedDocumentExtractor {
    @Override
    public boolean shouldParseEmbedded(Metadata metadata) {
        return false;
    }

    @Override
    public void parseEmbedded(InputStream inputStream, ContentHandler contentHandler, Metadata metadata, boolean b) {
    }
}
