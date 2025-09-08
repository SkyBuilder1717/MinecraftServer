package net.minecraft.server;

import java.io.*;
import java.util.*;

public class PluginConfig {
    private final Properties props = new Properties();
    private final File file;
    private final Map<String, String> defaultValues = new HashMap<>();

    public PluginConfig(File folder, String fileName) {
        if (!folder.exists()) folder.mkdirs();
        file = new File(folder, fileName);
        load();
    }

    public void setDefault(String key, String value) {
        defaultValues.put(key, value);
        if (!props.containsKey(key)) {
            props.setProperty(key, value);
            save();
        }
    }

    public void load() {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                MinecraftServer.logger.warning("Failed to load config: " + e.getMessage());
            }
        }

        for (Map.Entry<String, String> entry : defaultValues.entrySet()) {
            if (!props.containsKey(entry.getKey())) {
                props.setProperty(entry.getKey(), entry.getValue());
            }
        }
        save();
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Plugin Config");
        } catch (IOException e) {
            MinecraftServer.logger.warning("Failed to save config: " + e.getMessage());
        }
    }

    public String getString(String key, String def) {
        setDefault(key, def);
        return props.getProperty(key);
    }

    public int getInt(String key, int def) {
        setDefault(key, Integer.toString(def));
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        setDefault(key, Boolean.toString(def));
        return Boolean.parseBoolean(props.getProperty(key));
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }
}
