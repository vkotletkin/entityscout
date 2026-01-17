package ru.kotletkin.entityscout.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZipArchiveBuilder {

    public static byte[] createZipFromAttachments(Map<String, byte[]> attachments) throws IOException {

        ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(zipBuffer)) {
            for (Map.Entry<String, byte[]> entry : attachments.entrySet()) {
                String filename = entry.getKey();
                byte[] content = entry.getValue();

                ZipEntry zipEntry = new ZipEntry(filename);
                zipEntry.setSize(content.length);

                zos.putNextEntry(zipEntry);
                zos.write(content);
                zos.closeEntry();

            }
        }
        return zipBuffer.toByteArray();
    }
}
