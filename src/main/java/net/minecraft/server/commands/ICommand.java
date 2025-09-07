package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public interface ICommand {
    void execute(MinecraftServer server, ICommandListener sender, String[] args);
    boolean OnlyOP();
    String getParams();
    String getDescription();
}