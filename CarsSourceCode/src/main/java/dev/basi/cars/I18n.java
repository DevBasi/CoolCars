package dev.basi.cars;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author basi
 */
public final class I18n {

    private static final String[] SUPPORTED = new String[] { "en", "ru" };
    private static final String[] BUNDLE_PARTS = new String[] {
        "core.yml",
        "commands.yml",
        "ui.yml",
        "menu.yml",
        "help.yml",
    };

    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> bundles = new HashMap<>();
    private final Map<UUID, String> playerOverrides = new HashMap<>();

    private boolean usePlayerLocale;
    private boolean globalMode;
    private String defaultLang;
    private File playerLangFile;

    public I18n(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        ensureLangResourceExists("en");
        ensureLangResourceExists("ru");
        removeLegacyLangResource("uk");
        removeLegacyLangResource("ua");

        bundles.clear();
        for (String lang : SUPPORTED) {
            YamlConfiguration merged = new YamlConfiguration();
            for (String part : BUNDLE_PARTS) {
                String resourcePath = "lang/" + lang + "/" + part;
                File file = new File(plugin.getDataFolder(), resourcePath);
                loadAndMergeBundlePart(resourcePath, file, merged);
            }

            File legacyFile = new File(
                plugin.getDataFolder(),
                "lang/" + lang + ".yml"
            );
            if (legacyFile.isFile()) {
                try {
                    YamlConfiguration legacy = loadYamlFileSafe(legacyFile);
                    for (String key : legacy.getKeys(true)) {
                        Object value = legacy.get(key);
                        if (!(value instanceof String)) {
                            continue;
                        }
                        if (!merged.isSet(key)) {
                            merged.set(key, value);
                        }
                    }
                } catch (Exception ignored) {}
            }

            bundles.put(lang, merged);
        }

        this.defaultLang = normalizeLang(
            plugin.getConfig().getString("language.default", "en")
        );
        this.globalMode = plugin
            .getConfig()
            .getBoolean("language.global", true);
        this.usePlayerLocale = plugin
            .getConfig()
            .getBoolean("language.use-player-locale", true);

        this.playerLangFile = new File(
            plugin.getDataFolder(),
            "player-lang.yml"
        );
        loadOverrides();
    }

    private void backupCorruptedFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File backup = new File(
            file.getParentFile(),
            file.getName() + ".broken-" + System.currentTimeMillis()
        );
        if (!file.renameTo(backup)) {
            plugin
                .getLogger()
                .warning(
                    "Failed to backup corrupted lang file: " + file.getName()
                );
        }
    }

    private void ensureLangResourceExists(String lang) {
        for (String part : BUNDLE_PARTS) {
            String path = "lang/" + lang + "/" + part;
            File file = new File(plugin.getDataFolder(), path);
            if (file.exists()) {
                continue;
            }
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            plugin.saveResource(path, false);
        }
    }

    public String tr(CommandSender sender, String key, Object... args) {
        String lang = resolveSenderLang(sender);
        String template = raw(lang, key);
        return format(template, args);
    }

    public String trByLang(String lang, String key, Object... args) {
        String template = raw(normalizeLang(lang), key);
        return format(template, args);
    }

    public String resolveSenderLang(CommandSender sender) {
        if (sender instanceof Player player) {
            return resolvePlayerLang(player);
        }
        return defaultLang;
    }

    public String resolvePlayerLang(Player player) {
        if (globalMode) {
            return defaultLang;
        }
        String overridden = playerOverrides.get(player.getUniqueId());
        if (overridden != null) {
            return overridden;
        }
        if (usePlayerLocale) {
            return normalizeLang(player.getLocale());
        }
        return defaultLang;
    }

    public void setPlayerLanguage(Player player, String lang) {
        if (globalMode) {
            return;
        }
        playerOverrides.put(player.getUniqueId(), normalizeLang(lang));
        saveOverrides();
    }

    public void clearPlayerLanguage(Player player) {
        if (globalMode) {
            return;
        }
        playerOverrides.remove(player.getUniqueId());
        saveOverrides();
    }

    public boolean isGlobalMode() {
        return globalMode;
    }

    public String normalizeLang(String raw) {
        String value = raw == null ? "" : raw.toLowerCase(Locale.ROOT);
        if (value.startsWith("ru")) {
            return "ru";
        }
        if (value.startsWith("uk") || value.startsWith("ua")) {
            return "ru";
        }
        return "en";
    }

    private void removeLegacyLangResource(String lang) {
        File file = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
        if (file.exists() && !file.delete()) {
            plugin
                .getLogger()
                .warning(
                    "Failed to remove legacy language file: " + file.getName()
                );
        }
        File dir = new File(plugin.getDataFolder(), "lang/" + lang);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File child : files) {
                    child.delete();
                }
            }
            dir.delete();
        }
    }

    private void loadAndMergeBundlePart(
        String resourcePath,
        File file,
        YamlConfiguration merged
    ) {
        YamlConfiguration cfg;
        try {
            cfg = loadYamlFileSafe(file);
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning(
                    "Language file is corrupted (" +
                        file.getName() +
                        "), restoring from jar: " +
                        ex.getMessage()
                );
            backupCorruptedFile(file);
            plugin.saveResource(resourcePath, true);
            try {
                cfg = loadYamlFileSafe(file);
            } catch (Exception secondEx) {
                plugin
                    .getLogger()
                    .warning(
                        "Failed to restore language file " +
                            file.getName() +
                            ": " +
                            secondEx.getMessage()
                    );
                cfg = new YamlConfiguration();
            }
        }

        InputStream resource = plugin.getResource(resourcePath);
        if (resource != null) {
            try (
                InputStreamReader reader = new InputStreamReader(
                    resource,
                    StandardCharsets.UTF_8
                )
            ) {
                YamlConfiguration defaults = loadYamlReaderSafe(reader);
                cfg.setDefaults(defaults);
                cfg.options().copyDefaults(true);
                Files.writeString(file.toPath(), cfg.saveToString(), StandardCharsets.UTF_8);
                cfg = loadYamlFileSafe(file);
            } catch (Exception ex) {
                plugin
                    .getLogger()
                    .warning(
                        "Failed to merge lang defaults for " +
                            resourcePath +
                            ": " +
                            ex.getMessage()
                    );
            }
        }

        for (String key : cfg.getKeys(true)) {
            Object value = cfg.get(key);
            if (!(value instanceof String)) {
                continue;
            }
            merged.set(key, value);
        }
    }

    private String raw(String lang, String key) {
        FileConfiguration cfg = bundles.getOrDefault(lang, bundles.get("en"));
        Object localized = cfg == null ? null : cfg.get(key);
        if (localized instanceof String value && !value.isBlank()) {
            return color(value);
        }

        FileConfiguration en = bundles.get("en");
        Object enValue = en == null ? null : en.get(key);
        if (enValue instanceof String value && !value.isBlank()) {
            return color(value);
        }
        return color(key);
    }

    private static String format(String template, Object... args) {
        String out = template;
        for (int i = 0; i < args.length; i++) {
            out = out.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return out;
    }

    private static String color(String input) {
        return input.replace('&', '\u00A7');
    }

    private static YamlConfiguration loadYamlReaderSafe(
        InputStreamReader reader
    ) throws Exception {
        String raw = readAll(reader);
        String sanitized = sanitizeYamlText(raw);
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.load(new java.io.StringReader(sanitized));
        return cfg;
    }

    private static YamlConfiguration loadYamlFileSafe(File file)
        throws Exception {
        String raw = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        String sanitized = sanitizeYamlText(raw);
        if (!raw.equals(sanitized)) {
            Files.writeString(file.toPath(), sanitized, StandardCharsets.UTF_8);
        }
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.load(new java.io.StringReader(sanitized));
        return cfg;
    }

    private static String sanitizeYamlText(String text) {
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            boolean allowWhitespace = ch == '\n' || ch == '\r' || ch == '\t';
            boolean isControl = Character.isISOControl(ch);
            if (allowWhitespace || !isControl) {
                out.append(ch);
            }
        }
        return out.toString();
    }

    private static String readAll(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder(2048);
        char[] buffer = new char[2048];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, read);
        }
        return sb.toString();
    }

    private void loadOverrides() {
        playerOverrides.clear();
        if (playerLangFile == null || !playerLangFile.exists()) {
            return;
        }
        YamlConfiguration cfg;
        try (
            InputStreamReader reader = new InputStreamReader(
                new FileInputStream(playerLangFile),
                StandardCharsets.UTF_8
            )
        ) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin
                .getLogger()
                .warning(
                    "Could not load player languages file, it may be corrupted. A backup will be created."
                );
            backupCorruptedFile(playerLangFile);
            return;
        }
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                playerOverrides.put(
                    uuid,
                    normalizeLang(cfg.getString(key, "en"))
                );
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveOverrides() {
        if (playerLangFile == null) {
            return;
        }
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> e : playerOverrides.entrySet()) {
            cfg.set(e.getKey().toString(), e.getValue());
        }
        try {
            Files.writeString(playerLangFile.toPath(), cfg.saveToString(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning("Failed to save player-lang.yml: " + ex.getMessage());
        }
    }
}
