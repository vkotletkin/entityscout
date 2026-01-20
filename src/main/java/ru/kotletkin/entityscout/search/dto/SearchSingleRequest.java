package ru.kotletkin.entityscout.search.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchSingleRequest(@NotBlank String text, @NotBlank String query) {
}
