package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class DeopCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String name = args[0];
        server.configManager.deopPlayer(name);
        server.configManager.sendChatMessageToPlayer(name, "\247eYou are no longer op!");
        sender.log("De-opping " + name);
    }

    @Override
    public String getParams() {
        return "<player>";
    }

    @Override
    public String getDescription() {
        return "deops a player";
    }
}
