package ru.kotletkin.entityscout.document.extractor;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RecursiveAttachmentExtractor implements EmbeddedDocumentExtractor {

    private int depth = 0;
    private final int maximumDepth;
    private final Map<String, byte[]> attachments;

    public RecursiveAttachmentExtractor(Map<String, byte[]> attachments, int maximumDepth) {
        this.maximumDepth = maximumDepth;
        this.attachments = attachments;
    }

    @Override
    public boolean shouldParseEmbedded(Metadata metadata) {
        return depth < maximumDepth;
    }

    @Override
    public void parseEmbedded(InputStream inputStream, ContentHandler contentHandler, Metadata metadata, boolean b) throws IOException {

        String filename = metadata.get(TikaCoreProperties.RESOURCE_NAME_KEY);

        if (filename == null) {
            return;
        } else {

            if (filename.contains("/")) {
                filename = filename.substring(filename.lastIndexOf("/") + 1);
            }

            if (filename.contains("\\")) {
                filename = filename.substring(filename.lastIndexOf("\\") + 1);
            }

            if (filename.isEmpty()) {
                return;
            }
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int length;

        while ((length = inputStream.read(data)) != -1) {
            buffer.write(data, 0, length);
        }

        byte[] content = buffer.toByteArray();
        attachments.put(filename, content);

        depth++;

        try (InputStream embeddedStream = new ByteArrayInputStream(content)) {
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext embeddedContext = new ParseContext();
            embeddedContext.set(EmbeddedDocumentExtractor.class, this);
            parser.parse(embeddedStream, new DefaultHandler(), new Metadata(), embeddedContext);
        } catch (Exception e) {
            System.err.println("Error in parsing content: " + e.getMessage());
        } finally {
            depth--;
        }
    }
}
