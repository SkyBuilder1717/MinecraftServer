package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.Packet3Chat;

public class SayCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String msg = String.join(" ", args).substring(args[0].length()).trim();
        server.configManager.sendPacketToAllPlayers(new Packet3Chat("\247d[Server] " + msg));
    }

    @Override
    public String getParams() {
        return "<message>";
    }

    @Override
    public String getDescription() {
        return "says global message";
    }
}
