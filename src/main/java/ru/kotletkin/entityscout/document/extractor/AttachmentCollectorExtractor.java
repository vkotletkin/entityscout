package ru.kotletkin.entityscout.document.extractor;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AttachmentCollectorExtractor implements EmbeddedDocumentExtractor {

    private final Map<String, byte[]> attachments;

    public AttachmentCollectorExtractor(Map<String, byte[]> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean shouldParseEmbedded(Metadata metadata) {
        return true;
    }

    @Override
    public void parseEmbedded(InputStream inputStream, ContentHandler contentHandler, Metadata metadata, boolean b) throws SAXException, IOException {

        String filename = metadata.get(TikaCoreProperties.RESOURCE_NAME_KEY);

        if (filename == null) {
            filename = "attachment_" + System.nanoTime();
        }
// todo: check filename on decode Mime
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int length;

        while ((length = inputStream.read(data)) != -1) {
            buffer.write(data, 0, length);
        }

        attachments.put(filename, buffer.toByteArray());
    }
}
