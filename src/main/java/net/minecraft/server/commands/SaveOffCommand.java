package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class SaveOffCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        server.send_message(sender.getUsername(), "Disabling level saving...");
        server.worldMngr.field_816_A = false;
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
        return "disable auto-saving";
    }
}
