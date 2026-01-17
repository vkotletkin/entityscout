package ru.kotletkin.entityscout.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtils {

    private static final Pattern MULTI_SPACE = Pattern.compile(" {2,}");
    private static final Pattern MULTI_NEWLINE = Pattern.compile("\\n{2,}");
    private static final Pattern TRIM_LINES = Pattern.compile("(?m)^\\s+|\\s+$");

    public static String clean(String text) {
        if (text == null || text.isEmpty()) return null;
        text = TRIM_LINES.matcher(text).replaceAll("");
        text = MULTI_NEWLINE.matcher(text).replaceAll("\n");
        text = MULTI_SPACE.matcher(text).replaceAll(" ");
        return text.trim();
    }
}
