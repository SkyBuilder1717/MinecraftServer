package net.minecraft.server;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public abstract class SASPlugin {
    protected PluginDescription descriptionYaml;

    public SASPlugin() {
        loadDescription();
    }

    public abstract String getName();
    public abstract void onEnable(MinecraftServer server);
    public abstract void onDisable(MinecraftServer server);

    public PluginDescription getDescription() {
        return descriptionYaml;
    }

    private void loadDescription() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("plugin.yml")) {
            if (is == null) {
                MinecraftServer.logger.warning("plugin.yml not found for " + getName());
                descriptionYaml = new PluginDescription();
                descriptionYaml.name = getName();
                return;
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(is);

            descriptionYaml = new PluginDescription();
            descriptionYaml.name = (String) data.get("name");
            descriptionYaml.version = (String) data.get("version");
            descriptionYaml.author = (String) data.get("author");
            descriptionYaml.description = (String) data.get("description");
            descriptionYaml.main = (String) data.get("main");

            if (descriptionYaml.name == null) {
                MinecraftServer.logger.warning("Plugin " + getName() + " has no 'name' in plugin.yml!");
            }
            if (descriptionYaml.main == null) {
                MinecraftServer.logger.warning("Plugin " + getName() + " has no 'main' in plugin.yml!");
            }

        } catch (Exception e) {
            MinecraftServer.logger.warning("Failed to load plugin.yml for " + getName() + ": " + e.getMessage());
            descriptionYaml = new PluginDescription();
            descriptionYaml.name = getName();
        }
    }
}
