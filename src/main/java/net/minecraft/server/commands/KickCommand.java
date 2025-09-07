package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandListener;

public class KickCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) return;
        String name = args[0];
        EntityPlayerMP player = server.configManager.getPlayerEntity(name);
        if(player != null) {
            player.field_421_a.func_43_c("Kicked by admin");
            sender.log("Kicking " + name);
        } else {
            sender.log("Can't find player " + name);
        }
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
        return "kicks a player";
    }
}
