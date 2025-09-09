package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class ListCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(server.configManager.playerEntities.isEmpty()) sender.log("No players online.");
        sender.log("Online Players: " + server.configManager.getPlayerList() + ".");
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getDescription() {
        return "shows current online players";
    }
}
