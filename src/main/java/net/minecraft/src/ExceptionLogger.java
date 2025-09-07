package net.minecraft.src;

public class ExceptionLogger {
    public static void log(Exception e) {
        if (e.getMessage() == null) {
            net.minecraft.server.MinecraftServer.logger.warning("Unknown error occurred!");
            return;
        }
        net.minecraft.server.MinecraftServer.logger.warning("Error: " + e.getMessage());
    }
}