package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class BanIpCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String ip = args[0];
        server.configManager.banIP(ip);
        sender.log("Banning IP " + ip);
    }

    @Override
    public String getParams() {
        return "<ip>";
    }

    @Override
    public String getDescription() {
        return "bans a player by his ip";
    }
}
