package net.minecraft.server;

public interface SASPlugin {
    void onEnable(MinecraftServer server);
    void onDisable(MinecraftServer server);
    String getName();
    default EventListener getListener() {
        return null;
    }
    default PluginConfig createConfig() {
        return null;
    }

}
