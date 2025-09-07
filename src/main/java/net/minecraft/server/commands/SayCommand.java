package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.Packet3Chat;

public class SayCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if (args.length < 1) return;
        String msg = String.join(" ", args);

        String username = sender.getUsername();
        String rslt = "* " + username + " " + msg;
        if (server.configManager.isOp(username)) {
            rslt = "\247d[Server] " + msg;
        }
        server.configManager.sendPacketToAllPlayers(
                new Packet3Chat(rslt)
        );
    }

    @Override
    public boolean OnlyOP() {
        return false;
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