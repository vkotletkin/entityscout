package ru.kotletkin.entityscout.document.extractor;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AttachmentCollectorExtractor implements EmbeddedDocumentExtractor {

    private static final String ATTACHMENT_NAME_PREFIX = "attachment_";

    private final Map<String, byte[]> attachments;
    private final AtomicInteger counter = new AtomicInteger(0);

    public AttachmentCollectorExtractor(Map<String, byte[]> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean shouldParseEmbedded(Metadata metadata) {
        return true;
    }

    @Override
    public void parseEmbedded(InputStream inputStream, ContentHandler contentHandler, Metadata metadata, boolean b) throws IOException {

        int count = counter.incrementAndGet();
        if (count == 1) {
            return;
        }

        String filename = metadata.get(TikaCoreProperties.RESOURCE_NAME_KEY);

        if (filename == null) {
            filename = ATTACHMENT_NAME_PREFIX + System.nanoTime();
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int length;

        while ((length = inputStream.read(data)) != -1) {
            buffer.write(data, 0, length);
        }

        attachments.put(filename, buffer.toByteArray());
    }
}
