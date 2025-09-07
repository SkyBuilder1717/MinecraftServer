package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class StopCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        server.send_message(sender.getUsername(), "Stopping the server...");
        server.func_6016_a();
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
        return "stops the server";
    }
}
