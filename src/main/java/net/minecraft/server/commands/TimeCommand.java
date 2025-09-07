package net.minecraft.server.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.World;

public class TimeCommand implements ICommand {

    @Override
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) {
        World world = server.worldMngr;
        long ticks = world.worldTime % 24000;
        int totalMinutes = (int) (ticks * 60 / 1000);
        int hours = (6 + totalMinutes / 60) % 24;
        int minutes = totalMinutes % 60;
        if (args.length == 0) {
            sender.log(String.format("Current time: %02d:%02d", hours, minutes));
        } else {
            String arg = args[0];
            try {
                if (arg.contains(":")) {
                    String[] hm = arg.split(":");
                    int h = Integer.parseInt(hm[0]);
                    int m = Integer.parseInt(hm[1]);
                    world.worldTime = ((h - 6 + 24) % 24) * 1000 + (m * 1000L / 60);
                    sender.log("Time set to: " + String.format("%02d:%02d", h, m));
                } else {
                    long time = Long.parseLong(arg);
                    world.worldTime = time;
                    sender.log("Time set to: " + time);
                }
            } catch (NumberFormatException e) {
                sender.log("Invalid number or format! Use HH:MM or ticks.");
            }
        }
    }

    @Override
    public String getParams() {
        return "[num]";
    }

    @Override
    public String getDescription() {
        return "sets the time";
    }
}
