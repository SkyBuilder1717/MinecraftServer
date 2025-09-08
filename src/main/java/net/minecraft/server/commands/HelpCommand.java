package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerCommands;
import net.minecraft.src.ICommandListener;

public class HelpCommand implements ICommand {

    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        boolean is_op = server.configManager.isOp(sender.getUsername());
        if(args.length == 1) {
            String cmdName = args[0].toLowerCase();
            ICommand cmd = ServerCommands.getCommand(cmdName);
            if(cmd != null && (!cmd.OnlyOP() || (cmd.OnlyOP() && is_op))) {
                sender.log(cmdName + " " + cmd.getParams() + " - " + cmd.getDescription());
            } else {
                sender.log("No such command: " + cmdName);
            }
        } else {
            sender.log("Available commands:");
            for(String cmdName : ServerCommands.getAllCommandNames()) {
                ICommand cmd = ServerCommands.getCommand(cmdName);
                if (!cmd.OnlyOP() || (cmd.OnlyOP() && is_op)) {
                    sender.log("   " + cmdName + " " + cmd.getParams());
                }
            }
        }
    }

    @Override
    public String getParams() {
        return "[command]";
    }

    @Override
    public String getDescription() {
        return "shows all the commands";
    }
}
