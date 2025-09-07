package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.NetServerHandler;

public class TpCommand implements ICommand {

    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if (args.length == 0) {
            sender.log("Invalid usage! /tp <player> OR /tp <x> <y> <z> OR /tp <target> <destination>");
            return;
        }

        EntityPlayerMP playerToTeleport;
        double x, y, z;

        try {
            if (args.length == 1) {
                if (!(sender instanceof NetServerHandler)) {
                    sender.log("Only players can use this command!");
                    return;
                }
                playerToTeleport = ((NetServerHandler) sender).playerEntity;
                EntityPlayerMP target = server.configManager.getPlayerEntity(args[0]);
                if (target == null) {
                    sender.log("Can't find player " + args[0]);
                    return;
                }
                playerToTeleport.field_421_a.func_41_a(target.posX, target.posY, target.posZ, target.rotationYaw, target.rotationPitch);
                sender.log("Teleported to " + args[0]);
            } else if (args.length == 2) {
                EntityPlayerMP targetPlayer = server.configManager.getPlayerEntity(args[0]);
                EntityPlayerMP destinationPlayer = server.configManager.getPlayerEntity(args[1]);
                if (targetPlayer == null) {
                    sender.log("Can't find player " + args[0]);
                    return;
                }
                if (destinationPlayer == null) {
                    sender.log("Can't find player " + args[1]);
                    return;
                }
                targetPlayer.field_421_a.func_41_a(destinationPlayer.posX, destinationPlayer.posY, destinationPlayer.posZ, destinationPlayer.rotationYaw, destinationPlayer.rotationPitch);
                sender.log("Teleported " + args[0] + " to " + args[1]);
            } else if (args.length == 3) {
                if (!(sender instanceof NetServerHandler)) {
                    sender.log("Only players can use this command!");
                    return;
                }
                playerToTeleport = ((NetServerHandler) sender).playerEntity;
                x = Double.parseDouble(args[0]);
                y = Double.parseDouble(args[1]);
                z = Double.parseDouble(args[2]);
                playerToTeleport.field_421_a.func_41_a(x, y, z, playerToTeleport.rotationYaw, playerToTeleport.rotationPitch);
                sender.log("Teleported to coordinates: " + x + ", " + y + ", " + z);
            } else if (args.length == 4) {
                EntityPlayerMP targetPlayer = server.configManager.getPlayerEntity(args[0]);
                if (targetPlayer == null) {
                    sender.log("Can't find player " + args[0]);
                    return;
                }
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
                targetPlayer.field_421_a.func_41_a(x, y, z, targetPlayer.rotationYaw, targetPlayer.rotationPitch);
                sender.log("Teleported " + args[0] + " to coordinates: " + x + ", " + y + ", " + z);
            } else {
                sender.log("Invalid usage! /tp <player> OR /tp <x> <y> <z> OR /tp <target> <destination>");
            }
        } catch (NumberFormatException e) {
            sender.log("Invalid coordinates!");
        }
    }

    @Override
    public String getParams() {
        return "<player> OR <x> <y> <z> OR <target> <destination>";
    }

    @Override
    public String getDescription() {
        return "Teleports players to others or coordinates";
    }
}