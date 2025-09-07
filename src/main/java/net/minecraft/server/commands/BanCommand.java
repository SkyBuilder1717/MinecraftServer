package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandListener;

public class BanCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String name = args[0];
        server.configManager.banPlayer(name);
        EntityPlayerMP player = server.configManager.getPlayerEntity(name);
        if(player != null) player.field_421_a.func_43_c("Banned by admin");
        sender.log("Banning " + name);
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
        return "bans a player";
    }
}
