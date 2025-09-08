package net.minecraft.server;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@SuppressWarnings("resource")
public class PluginManager {
    private final List<SASPlugin> plugins = new ArrayList<>();

    public void loadPlugins(MinecraftServer server) {
        File pluginDir = new File("plugins");
        if (!pluginDir.exists() && !pluginDir.mkdirs()) {
            MinecraftServer.logger.warning("Cannot create \"plugin\" folder! Do jar file have right permissions?");
        }

        File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return;

        for (File file : files) {
            try {
                URLClassLoader loader = new URLClassLoader(
                        new URL[]{file.toURI().toURL()},
                        server.getClass().getClassLoader()
                );
                String mainClass = new java.util.jar.JarFile(file)
                        .getManifest().getMainAttributes().getValue("Main-Class");

                if (mainClass == null) {
                    MinecraftServer.logger.warning("Plugin " + file.getName() + " has no Main-Class!");
                    continue;
                }

                Class<?> clazz = loader.loadClass(mainClass);
                Object obj = clazz.getDeclaredConstructor().newInstance();

                if (obj instanceof SASPlugin) {
                    SASPlugin plugin = (SASPlugin) obj;
                    plugin.onEnable(server);
                    plugins.add(plugin);
                    plugin.createConfig();
                    MinecraftServer.logger.info("Loaded plugin: " + plugin.getName());
                } else {
                    MinecraftServer.logger.warning("Class " + mainClass + " does not implement Plugin!");
                }
            } catch (Exception e) {
                MinecraftServer.logger.warning("Failed to load plugin " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    public void disablePlugins(MinecraftServer server) {
        for (SASPlugin plugin : plugins) {
            try {
                plugin.onDisable(server);
                MinecraftServer.logger.info("Disabled plugin: " + plugin.getName());
            } catch (Exception e) {
                MinecraftServer.logger.warning("Error disabling plugin " + plugin.getName() + ": " + e.getMessage());
            }
        }
    }

    private final Map<SASPlugin, PluginConfig> pluginConfigs = new HashMap<>();

    public PluginConfig createConfig(SASPlugin plugin, String fileName) {
        File folder;
        String finalFileName;

        if (fileName.contains("/") || fileName.contains("\\")) {
            String path = fileName.replace("\\", "/");
            int lastSlash = path.lastIndexOf("/");
            folder = new File("plugins/" + plugin.getName() + "/" + path.substring(0, lastSlash));
            finalFileName = path.substring(lastSlash + 1);
        } else {
            folder = new File("plugins/" + plugin.getName());
            finalFileName = fileName;
        }

        PluginConfig config = new PluginConfig(folder, finalFileName);
        pluginConfigs.put(plugin, config);
        return config;
    }

    public PluginConfig getConfig(SASPlugin plugin) {
        return pluginConfigs.get(plugin);
    }

    private final List<EventListener> listeners = new ArrayList<>();

    public void registerListener(EventListener listener) {
        if (listener != null) listeners.add(listener);
    }

    public List<EventListener> getListeners() {
        return listeners;
    }
}
