package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class SaveAllCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        server.send_message(sender.getUsername(), "Forcing save...");
        server.worldMngr.func_485_a(true, null);
        server.send_message(sender.getUsername(), "Save complete.");
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getDescription() {
        return "force save";
    }
}
