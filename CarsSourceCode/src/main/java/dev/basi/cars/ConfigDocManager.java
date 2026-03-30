package dev.basi.cars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author basi
 */
public final class ConfigDocManager {

    private static final String RU_DOC_RESOURCE =
        "docs/VolgaDocRu/volga-doc.ru.yml";
    private static final String EN_DOC_RESOURCE =
        "docs/VolgaDocEn/volga-doc.en.yml";

    private final JavaPlugin plugin;

    public ConfigDocManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void sync(String langRaw) {
        String lang = normalize(langRaw);

        plugin.saveResource(EN_DOC_RESOURCE, true);
        plugin.saveResource(RU_DOC_RESOURCE, true);
        removeLegacyDocFiles();

        File dataFolder = plugin.getDataFolder();
        File src = new File(
            dataFolder,
            lang.equals("ru") ? RU_DOC_RESOURCE : EN_DOC_RESOURCE
        );
        File dst = new File(dataFolder, "VolgaDoc.yml");

        if (!src.exists()) {
            plugin
                .getLogger()
                .warning(
                    "Volga doc template not found: " + src.getAbsolutePath()
                );
            return;
        }

        try {
            Files.copy(
                src.toPath(),
                dst.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException ex) {
            plugin
                .getLogger()
                .warning("Failed to sync VolgaDoc.yml: " + ex.getMessage());
        }
    }

    private static String normalize(String raw) {
        if (raw == null) {
            return "en";
        }
        String value = raw.toLowerCase();
        if (value.startsWith("ru")) {
            return "ru";
        }
        if (value.startsWith("uk") || value.startsWith("ua")) {
            return "ru";
        }
        return "en";
    }

    private void removeLegacyDocFiles() {
        File[] legacy = new File[] {
            new File(plugin.getDataFolder(), "docs/config-doc.en.yml"),
            new File(plugin.getDataFolder(), "docs/config-doc.ru.yml"),
            new File(plugin.getDataFolder(), "docs/config-doc.uk.yml"),
            new File(plugin.getDataFolder(), "config-doc.yml"),
        };
        for (File file : legacy) {
            if (file.exists() && !file.delete()) {
                plugin
                    .getLogger()
                    .warning(
                        "Failed to remove legacy doc file: " + file.getName()
                    );
            }
        }
    }
}

