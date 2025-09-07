package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class GiveCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 2) return;
        EntityPlayerMP player = server.configManager.getPlayerEntity(args[0]);
        if(player == null) {
            sender.log("Can't find player " + args[0]);
            return;
        }

        try {
            int itemId = Integer.parseInt(args[1]);
            int count = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
            if(Item.itemsList[itemId] != null) {
                player.field_421_a.sendPacket(new Packet17AddToInventory(new ItemStack(itemId, count), count));
                sender.log("Giving " + args[0] + " id " + itemId);
            } else {
                sender.log("Invalid item id " + itemId);
            }
        } catch(Exception e) {
            sender.log("Invalid number format.");
        }
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "<player> <item> [count]";
    }

    @Override
    public String getDescription() {
        return "gives an item to the player";
    }
}
