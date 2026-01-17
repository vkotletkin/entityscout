package ru.kotletkin.entityscout.document.dto;

import java.util.Map;

public record DocumentInfo(String authorName, String language, String contentType, String text,
                           Map<String, String> metadata) {
}
