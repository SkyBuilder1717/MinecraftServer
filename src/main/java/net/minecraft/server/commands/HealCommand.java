package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.NetServerHandler;

public class HealCommand implements ICommand {

    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        EntityPlayerMP target = null;

        if(args.length >= 1) {
            target = server.configManager.getPlayerEntity(args[0]);
            if(target == null) {
                sender.log("Player not found: " + args[0]);
                return;
            }
        } else {
            if(sender instanceof NetServerHandler) {
                target = ((NetServerHandler) sender).playerEntity;
            } else {
                sender.log("You must specify a player when executing this command from the console.");
                return;
            }
        }

        target.field_9109_aQ = target.field_9099_av;
        sender.log(target.username + " has been healed!");
        if(sender != target) {
            server.configManager.sendChatMessageToPlayer(target.username, "You have been healed by " + sender.getUsername() + "!");
        }
    }

    @Override
    public String getParams() {
        return "[player]";
    }

    @Override
    public String getDescription() {
        return "heals a player";
    }
}
