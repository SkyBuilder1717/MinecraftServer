package net.minecraft.server;

import net.minecraft.src.*;

public interface EventListener {
    default void onPlayerJoin(EntityPlayerMP player, double x, double y, double z) {}
    default void onPlayerQuit(EntityPlayerMP player, double x, double y, double z) {}
    default void onPlayerAttack(EntityPlayerMP player, Entity target, int damage) {}
    default void onPlayerDeath(EntityPlayerMP player, Entity reason, double x, double y, double z) {}
    default void onPlayerRespawn(EntityPlayerMP player, double x, double y, double z) {}
    default void onPlayerMove(EntityPlayerMP player, double x, double y, double z) {}
    default void onPlayerInteract(EntityPlayer player, ItemStack itemstack) {}
    default void onPlayerCollideWithPlayer(EntityPlayer collider, EntityPlayer player) {}
    default void onPlayerLogin(String username) {}
    default void onPlayerKick(String player, String reason) {}
    default void onPlayerChat(EntityPlayerMP player, String message) {}
    default void onPlayerCommand(ICommandListener sender, String command, String[] args) {}

    default void onBlockPlace(EntityPlayerMP player, int x, int y, int z, int blockId) {}
    default void onBlockBreak(EntityPlayerMP player, int x, int y, int z, int blockId) {}
    default void onExplosion(Entity exploder, double x, double y, double z, float power) {}

    default void onEntitySpawn(Entity entity, double x, double y, double z) {}
    default void onEntityCollideWithPlayer(Entity collider, EntityPlayer player) {}
    default void onEntityDeath(Entity entity, Entity reason, double x, double y, double z) {}

    default void onServerTick(MinecraftServer server) {}
}