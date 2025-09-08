package net.minecraft.server;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public abstract class SASPlugin {
    protected PluginDescription desc;

    public SASPlugin() {
        loadDescription();
    }

    public abstract String getName();
    public abstract void onEnable(MinecraftServer server);
    public abstract void onDisable(MinecraftServer server);

    public PluginDescription getDescription() {
        return desc;
    }

    private void loadDescription() {
        desc = new PluginDescription();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("plugin.yml")) {
            if (is == null) {
                MinecraftServer.logger.warning("plugin.yml not found for " + getName());
                desc.name = getName();
                return;
            }

            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(is);

            desc.name = data.get("name") != null ? data.get("name").toString() : getName();
            desc.version = data.get("version") != null ? data.get("version").toString() : "";
            desc.author = data.get("author") != null ? data.get("author").toString() : "";
            desc.description = data.get("description") != null ? data.get("description").toString() : "";
            desc.main = data.get("main") != null ? data.get("main").toString() : null;

            if (desc.name == null || desc.name.isEmpty()) {
                MinecraftServer.logger.warning("Plugin " + getName() + " has no 'name' in plugin.yml!");
                desc.name = getName();
            }
            if (desc.main == null || desc.main.isEmpty()) {
                MinecraftServer.logger.warning("Plugin " + getName() + " has no 'main' in plugin.yml!");
            }

        } catch (Exception e) {
            MinecraftServer.logger.warning("Failed to load plugin.yml for " + getName() + ": " + e.getMessage());
            desc.name = getName();
        }
    }
}
