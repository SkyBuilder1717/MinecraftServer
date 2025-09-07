package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.text.DecimalFormat;

public class SummonCommand implements ICommand {
    
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) {
            sender.log("Usage: /summon <entity> [player|x y z]");
            return;
        }

        String entityName = args[0];
        double x, y, z;
        EntityPlayerMP targetPlayer;

        if(args.length == 2) {
            targetPlayer = server.configManager.getPlayerEntity(args[1]);
            if(targetPlayer == null) {
                sender.log("Player not found: " + args[1]);
                return;
            }
            x = targetPlayer.posX;
            y = targetPlayer.posY;
            z = targetPlayer.posZ;
        } else if(args.length == 4) {
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch(NumberFormatException e) {
                sender.log("Invalid coordinates.");
                return;
            }
        } else if (sender instanceof NetServerHandler) {
            EntityPlayerMP player = ((NetServerHandler) sender).playerEntity;
            x = player.posX;
            y = player.posY;
            z = player.posZ;
        } else {
            sender.log("You must specify a player or coordinates when executing from console.");
            return;
        }

        Entity entity = EntityRegistry.create(entityName, server.worldMngr);
        if(entity == null) {
            sender.log("Unknown entity: " + entityName);
            return;
        }
        
        entity.func_107_c(x, y, z, 90F, 0F);
        server.worldMngr.entityJoinedWorld(entity);
        DecimalFormat df = new DecimalFormat("#.#");
        sender.log("Summoned " + entityName + " at " + df.format(x) + ", " + df.format(y) + ", " + df.format(z));
    }

    @Override
    public String getParams() {
        return "<entity> [player|x y z]";
    }

    @Override
    public String getDescription() {
        return "spawns a mob";
    }
}
