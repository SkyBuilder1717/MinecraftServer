package net.minecraft.server;

import net.minecraft.server.commands.ICommand;
import net.minecraft.src.ICommandListener;
import java.util.HashMap;

public class ServerCommands {

    private static final HashMap<String, ICommand> commands = new HashMap<>();

    public static void register(String name, ICommand command) {
        commands.put(name.toLowerCase(), command);
    }

    public static boolean execute(MinecraftServer server, ICommandListener sender, String fullCommand) {
        String[] parts = fullCommand.trim().split(" ");
        String cmdName = parts[0].toLowerCase();
        ICommand command = commands.get(cmdName);
        if (command != null) {
            command.execute(server, sender, parts);
            return true;
        }
        return false;
    }

    public static ICommand getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public static Iterable<String> getAllCommandNames() {
        return commands.keySet();
    }

}
