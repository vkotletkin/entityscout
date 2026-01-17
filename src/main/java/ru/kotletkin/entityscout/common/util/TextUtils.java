package ru.kotletkin.entityscout.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtils {

    private static final Pattern CLEAR_TEXT_PATTERN = Pattern.compile("[\n\r\t ]+");

    public static String clean(String text) {
        if (text == null || text.isEmpty()) return "";
        text = CLEAR_TEXT_PATTERN.matcher(text).replaceAll(" ");
        return text.trim();
    }
}
