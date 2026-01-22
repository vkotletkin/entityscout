package ru.kotletkin.entityscout.language.dto;


import org.apache.tika.language.detect.LanguageResult;

public record LanguageDetectionDTO(String language, LanguageResult details) {
}
