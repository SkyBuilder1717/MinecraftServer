package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class VanishCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(!(sender instanceof NetServerHandler)) {
            sender.log("Only players can vanish!");
            return;
        }

        EntityPlayerMP player = ((NetServerHandler) sender).playerEntity;
        if(!server.configManager.isVanished(player)) {
            server.configManager.hidePlayer(player);
            sender.log("You are now vanished!");
        } else {
            server.configManager.showPlayer(player);
            sender.log("You are now visible!");
        }
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getDescription() {
        return "vanish a player";
    }
}
