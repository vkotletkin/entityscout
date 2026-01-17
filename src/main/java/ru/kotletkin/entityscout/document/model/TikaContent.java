package ru.kotletkin.entityscout.document.model;

import java.util.Map;

public record TikaContent(
        String resourceName,
        String author,
        String title,
        String contentType,
        String text,
        String isEncrypted,
        Map<String, String> metadata
) {
}
