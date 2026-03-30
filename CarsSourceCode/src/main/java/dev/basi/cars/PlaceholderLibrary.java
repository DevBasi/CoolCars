package dev.basi.cars;

import java.util.Map;

/**
 * @author basi
 */
public final class PlaceholderLibrary {

    private PlaceholderLibrary() {}

    public static String apply(String template, Map<String, String> values) {
        if (template == null || template.isEmpty() || values == null || values.isEmpty()) {
            return template == null ? "" : template;
        }

        String out = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isBlank()) {
                continue;
            }
            String value = entry.getValue() == null ? "" : entry.getValue();
            out = out.replace("{" + key + "}", value);
        }
        return out;
    }
}


