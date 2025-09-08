package net.minecraft.server.commands;

import net.minecraft.server.*;
import net.minecraft.src.*;
import java.util.List;

public class PluginsCommand implements ICommand {

    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        List<SASPlugin> plugins = server.pluginManager.getPlugins();

        if (args.length == 0) {
            for (SASPlugin pl : plugins) {
                PluginDescription plugin = pl.getDescription();
                String version = plugin.version != null && !plugin.version.isEmpty() ? ": v" + plugin.version : "";
                sender.log(pl.getName() + version);
            }
        } else {
            String searchName = args[0].toLowerCase();
            PluginDescription found = null;
            for (SASPlugin pl : plugins) {
                if (pl.getName().toLowerCase().equals(searchName)) {
                    PluginDescription plugin = pl.getDescription();
                    found = plugin;
                    break;
                }
            }

            if (found != null) {
                String description = found.description != null && !found.description.isEmpty() ? ": " + found.description : "";
                sender.log(found.name + description);

                String version = found.version != null && !found.version.isEmpty() ? "Version: " + found.version : "";
                String author = found.author != null && !found.author.isEmpty() ? "by " + found.author : "";

                if (!version.isEmpty() || !author.isEmpty()) {
                    sender.log((author.isEmpty() ? "" : author + "; ") + version);
                }
            } else {
                sender.log("Plugin not found: " + args[0]);
            }
        }
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "[plugin]";
    }

    @Override
    public String getDescription() {
        return "shows all plugins";
    }
}
