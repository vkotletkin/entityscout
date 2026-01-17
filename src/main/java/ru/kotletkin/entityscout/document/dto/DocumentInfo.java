package ru.kotletkin.entityscout.document.dto;

import java.util.Map;

public record DocumentInfo(String resourceName,
                           String language,
                           String title,
                           String contentType,
                           String text,
                           String isEncrypted,
                           Map<String, String> metadata) {
}
