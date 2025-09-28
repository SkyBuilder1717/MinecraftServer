package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;
import java.util.Objects;

public class WhitelistCommand implements ICommand {
    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        if(args.length < 1) {
            if (server.configManager.getWhiteListedPlayers().isEmpty()) {
                sender.log("No whitelisted players.");
            } else {
                sender.log("Whitelist: " + server.configManager.getWhitelist());
            }
            return;
        } else if (args.length >= 2) {
            String action = args[0];
            String player = args[1];
            if (Objects.equals(action, "add")) {
                server.configManager.whitelistPlayer(player);
                sender.log("Whitelisted: " + player);
            } else if (Objects.equals(action, "remove")) {
                server.configManager.removeWhitelistPlayer(player);
                sender.log("Removed from whitelist: " + player);
            }
            return;
        }

        sender.log("Syntax error.");
    }

    @Override
    public boolean OnlyOP() {
        return true;
    }

    @Override
    public String getParams() {
        return "<add|remove> [player]";
    }

    @Override
    public String getDescription() {
        return "manage whitelist";
    }
}
