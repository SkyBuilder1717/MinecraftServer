package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class PardonCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String name = args[0];
        server.configManager.unbanPlayer(name);
        sender.log("Pardoning " + name);
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "<player>";
    }

    @Override
    public String getDescription() {
        return "pardons a player";
    }
}
