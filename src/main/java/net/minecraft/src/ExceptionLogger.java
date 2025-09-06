package net.minecraft.src;

public class ExceptionLogger {
    public static void log(Exception e) {
        net.minecraft.server.MinecraftServer.logger.warning(e.getMessage());
    }
}
