package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class PardonIpCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String ip = args[0];
        server.configManager.unbanIP(ip);
        sender.log("Pardoning IP " + ip);
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "<ip>";
    }

    @Override
    public String getDescription() {
        return "pardons a player by his ip";
    }
}
