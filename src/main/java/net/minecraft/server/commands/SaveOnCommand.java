package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class SaveOnCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        server.send_message(sender.getUsername(), "Enabling level saving...");
        server.worldMngr.field_816_A = true;
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
        return "enables auto-saving";
    }
}
