package net.minecraft.server.commands;

import net.minecraft.server.*;
import net.minecraft.src.*;

import java.util.Objects;

public class ClearCommand implements ICommand {

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

        target.inventory.mainInventory = new ItemStack[37];

        String username = target.username;
        if (Objects.equals(sender.getUsername(), target.username)) username = "You";

        sender.log(username + " have been cleared.");
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "[player]";
    }

    @Override
    public String getDescription() {
        return "clears players inventory";
    }
}
