package ru.kotletkin.entityscout.document.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType {
    AUTO(""),
    RFC822("message/rfc822");

    private final String contentTypeOverride;
}
