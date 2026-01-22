package ru.kotletkin.entityscout.common.dto;

import jakarta.validation.constraints.NotBlank;

public record BaseRequest(@NotBlank String text) {
}
