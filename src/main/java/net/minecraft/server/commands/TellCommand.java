package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;

public class TellCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if (args.length < 2) {
            sender.log("Usage: tell <player> <message>");
            return;
        }

        String target = args[0];

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) sb.append(" ");
            sb.append(args[i]);
        }
        String msg = sb.toString();

        server.configManager.sendChatMessageToPlayer(target, "\2477" + sender.getUsername() + " whispers: " + msg);
        sender.log("Sent message to " + target + ": " + msg);
    }

    @Override
    public boolean OnlyOP() {
        return false;
    }

    @Override
    public String getParams() {
        return "<player> <message>";
    }

    @Override
    public String getDescription() {
        return "Sends a private message to the player";
    }
}
