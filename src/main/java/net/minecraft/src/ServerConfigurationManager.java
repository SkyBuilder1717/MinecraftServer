package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import net.minecraft.server.*;
import net.minecraft.server.EventListener;

public class ServerConfigurationManager
{

    public ServerConfigurationManager(MinecraftServer minecraftserver)
    {
        playerEntities = new ArrayList();
        whiteListedPlayers = new HashSet();
        bannedPlayers = new HashSet();
        bannedIPs = new HashSet();
        ops = new HashSet();
        mcServer = minecraftserver;
        bannedPlayersFile = minecraftserver.getFile("banned-players.txt");
        ipBanFile = minecraftserver.getFile("banned-ips.txt");
        opFile = minecraftserver.getFile("ops.txt");
        whiteListedPlayersFile = minecraftserver.getFile("whitelist.txt");
        playerManagerObj = new PlayerManager(minecraftserver);
        maxPlayers = minecraftserver.propertyManagerObj.getIntProperty("max-players", 20);
        readBannedPlayers();
        readWhiteListedPlayers();
        loadBannedList();
        loadOps();
        writeBannedPlayers();
        writeWhiteListedPlayers();
        saveBannedList();
        saveOps();
    }

    public void setPlayerManager(WorldServer worldserver)
    {
        playerNBTManagerObj = new PlayerNBTManager(new File(worldserver.field_797_s, "players"));
    }

    public int func_640_a()
    {
        return playerManagerObj.func_542_b();
    }

    public void playerLoggedIn(EntityPlayerMP entityplayermp)
    {
        playerEntities.add(entityplayermp);
        playerNBTManagerObj.readPlayerData(entityplayermp);
        mcServer.worldMngr.field_821.loadChunk((int)entityplayermp.posX >> 4, (int)entityplayermp.posZ >> 4);
        for(; mcServer.worldMngr.getCollidingBoundingBoxes(entityplayermp, entityplayermp.boundingBox).size() != 0; entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0D, entityplayermp.posZ)) { }
        mcServer.worldMngr.entityJoinedWorld(entityplayermp);
        playerManagerObj.func_9214_a(entityplayermp);
        for (EventListener l : mcServer.pluginManager.getListeners()) {
            l.onPlayerJoin(entityplayermp, entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ);
        }
    }

    public void hidePlayer(EntityPlayerMP player) {
        mcServer.vanishedPlayers.put(player, true);
        Packet29DestroyEntity destroy = new Packet29DestroyEntity(player.field_331_c);
        for (EntityPlayerMP o : playerEntities) {
            EntityPlayerMP other = o;
            if (other != player) {
                other.field_421_a.sendPacket(destroy);
            }
        }
    }

    public void showPlayer(EntityPlayerMP player) {
        mcServer.vanishedPlayers.remove(player);
        for (EntityPlayerMP o : mcServer.worldMngr.playerEntities) {
            EntityPlayerMP other = o;
            if (other != player && !mcServer.configManager.isVanished(other)) {
                other.field_421_a.sendPacket(new Packet20NamedEntitySpawn(player));
            }
        }
    }

    public void func_613_b(EntityPlayerMP entityplayermp)
    {
        playerManagerObj.func_543_c(entityplayermp);
    }

    public void playerLoggedOut(EntityPlayerMP entityplayermp)
    {
        playerNBTManagerObj.writePlayerData(entityplayermp);
        mcServer.worldMngr.func_12016_d(entityplayermp);
        playerEntities.remove(entityplayermp);
        if (isVanished(entityplayermp)) mcServer.vanishedPlayers.remove(entityplayermp);
        playerManagerObj.func_9213_b(entityplayermp);
        for (EventListener l : mcServer.pluginManager.getListeners()) {
            l.onPlayerQuit(entityplayermp, entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ);
        }
    }

    public EntityPlayerMP login(NetLoginHandler netloginhandler, String s, String s1)
    {
        String nickname = s.trim().toLowerCase();
        if(bannedPlayers.contains(nickname))
        {
            netloginhandler.kickUser("You are banned from this server!");
            return null;
        }
        String s2 = netloginhandler.netManager.getRemoteAddress().toString();
        s2 = s2.substring(s2.indexOf("/") + 1);
        s2 = s2.substring(0, s2.indexOf(":"));
        if(bannedIPs.contains(s2))
        {
            netloginhandler.kickUser("Your IP address is banned from this server!");
            return null;
        }
        if(mcServer.whiteList && !isOp(nickname) && !whiteListedPlayers.contains(nickname))
        {
            netloginhandler.kickUser("You are not whitelisted on this server!");
            return null;
        }
        if(playerEntities.size() >= maxPlayers)
        {
            netloginhandler.kickUser("The server is full!");
            return null;
        }
        for(int i = 0; i < playerEntities.size(); i++)
        {
            EntityPlayerMP entityplayermp = playerEntities.get(i);
            if(entityplayermp.username.equalsIgnoreCase(s))
            {
                entityplayermp.field_421_a.func_43_c("You logged in from another location");
            }
        }

        return new EntityPlayerMP(mcServer, mcServer.worldMngr, s, new ItemInWorldManager(mcServer.worldMngr));
    }

    public EntityPlayerMP func_9242_d(EntityPlayerMP entityplayermp)
    {
        mcServer.field_6028_k.func_9238_a(entityplayermp);
        mcServer.field_6028_k.func_610_b(entityplayermp);
        playerManagerObj.func_9213_b(entityplayermp);
        playerEntities.remove(entityplayermp);
        mcServer.worldMngr.func_12014_e(entityplayermp);
        EntityPlayerMP entityplayermp1 = new EntityPlayerMP(mcServer, mcServer.worldMngr, entityplayermp.username, new ItemInWorldManager(mcServer.worldMngr));
        entityplayermp1.field_331_c = entityplayermp.field_331_c;
        entityplayermp1.field_421_a = entityplayermp.field_421_a;
        mcServer.worldMngr.field_821.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);
        for(; mcServer.worldMngr.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).size() != 0; entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ)) { }
        entityplayermp1.field_421_a.sendPacket(new Packet9());
        entityplayermp1.field_421_a.func_41_a(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
        playerManagerObj.func_9214_a(entityplayermp1);
        mcServer.worldMngr.entityJoinedWorld(entityplayermp1);
        playerEntities.add(entityplayermp1);
        for (EventListener l : MinecraftServer.instance.pluginManager.getListeners()) {
            l.onPlayerRespawn(entityplayermp1, entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ);
        }
        return entityplayermp1;
    }

    public void func_637_b()
    {
        playerManagerObj.func_538_a();
    }

    public void func_622_a(int i, int j, int k)
    {
        playerManagerObj.func_535_a(i, j, k);
    }

    public void sendPacketToAllPlayers(Packet packet)
    {
        for(int i = 0; i < playerEntities.size(); i++)
        {
            EntityPlayerMP entityplayermp = playerEntities.get(i);
            entityplayermp.field_421_a.sendPacket(packet);
        }
    }

    public boolean isVanished(EntityPlayer player) {
        return mcServer.vanishedPlayers.containsKey(player);
    }

    public String getPlayerList()
    {
        StringBuilder s = new StringBuilder();
        List<EntityPlayerMP> visiblePlayers = new ArrayList<>();
        for(int i = 0; i < playerEntities.size(); i++) {
            if (!isVanished(playerEntities.get(i))) visiblePlayers.add(playerEntities.get(i));
        }
        
        for(int i = 0; i < visiblePlayers.size(); i++)
        {
            if(i > 0)
            {
                s.append(", ");
            }
            s.append(playerEntities.get(i).username);
        }

        return s.toString();
    }

    public String getWhitelist()
    {
        StringBuilder s = new StringBuilder();

        for(int i = 0; i < whiteListedPlayers.size(); i++)
        {
            if(i > 0)
            {
                s.append(", ");
            }
            s.append(whiteListedPlayers.toArray()[i]);
        }

        return s.toString();
    }

    public void whitelistPlayer(String s)
    {
        whiteListedPlayers.add(s.toLowerCase());
        writeWhiteListedPlayers();
    }

    public void removeWhitelistPlayer(String s)
    {
        whiteListedPlayers.remove(s.toLowerCase());
        writeWhiteListedPlayers();
    }

    private void readWhiteListedPlayers()
    {
        try
        {
            whiteListedPlayers.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(whiteListedPlayersFile));
            for(String s = ""; (s = bufferedreader.readLine()) != null;)
            {
                whiteListedPlayers.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to load whitelist: ").append(exception).toString());
        }
    }

    private void writeWhiteListedPlayers()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(whiteListedPlayersFile, false));
            String s;
            for(Iterator iterator = whiteListedPlayers.iterator(); iterator.hasNext(); printwriter.println(s))
            {
                s = (String)iterator.next();
            }

            printwriter.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to save whitelist: ").append(exception).toString());
        }
    }

    public void banPlayer(String s)
    {
        bannedPlayers.add(s.toLowerCase());
        writeBannedPlayers();
    }

    public void unbanPlayer(String s)
    {
        bannedPlayers.remove(s.toLowerCase());
        writeBannedPlayers();
    }

    private void readBannedPlayers()
    {
        try
        {
            bannedPlayers.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(bannedPlayersFile));
            for(String s = ""; (s = bufferedreader.readLine()) != null;)
            {
                bannedPlayers.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to load ban list: ").append(exception).toString());
        }
    }

    private void writeBannedPlayers()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(bannedPlayersFile, false));
            String s;
            for(Iterator iterator = bannedPlayers.iterator(); iterator.hasNext(); printwriter.println(s))
            {
                s = (String)iterator.next();
            }

            printwriter.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to save ban list: ").append(exception).toString());
        }
    }

    public void banIP(String s)
    {
        bannedIPs.add(s.toLowerCase());
        saveBannedList();
    }

    public void unbanIP(String s)
    {
        bannedIPs.remove(s.toLowerCase());
        saveBannedList();
    }

    private void loadBannedList()
    {
        try
        {
            bannedIPs.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(ipBanFile));
            for(String s = ""; (s = bufferedreader.readLine()) != null;)
            {
                bannedIPs.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to load ip ban list: ").append(exception).toString());
        }
    }

    private void saveBannedList()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(ipBanFile, false));
            String s;
            for(Iterator iterator = bannedIPs.iterator(); iterator.hasNext(); printwriter.println(s))
            {
                s = (String)iterator.next();
            }

            printwriter.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to save ip ban list: ").append(exception).toString());
        }
    }

    public void opPlayer(String s)
    {
        ops.add(s.toLowerCase());
        saveOps();
    }

    public void deopPlayer(String s)
    {
        ops.remove(s.toLowerCase());
        saveOps();
    }

    private void loadOps()
    {
        try
        {
            ops.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(opFile));
            for(String s = ""; (s = bufferedreader.readLine()) != null;)
            {
                ops.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to load ip ban list: ").append(exception).toString());
        }
    }

    private void saveOps()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(opFile, false));
            String s;
            for(Iterator iterator = ops.iterator(); iterator.hasNext(); printwriter.println(s))
            {
                s = (String)iterator.next();
            }

            printwriter.close();
        }
        catch(Exception exception)
        {
            logger.warning((new StringBuilder()).append("Failed to save ip ban list: ").append(exception).toString());
        }
    }

    public boolean isOp(String s)
    {
        return s.trim().equalsIgnoreCase("console") || ops.contains(s.trim().toLowerCase());
    }

    public EntityPlayerMP getPlayerEntity(String s)
    {
        for(int i = 0; i < playerEntities.size(); i++)
        {
            EntityPlayerMP entityplayermp = playerEntities.get(i);
            if(entityplayermp.username.equalsIgnoreCase(s))
            {
                return entityplayermp;
            }
        }

        return null;
    }

    public void sendChatMessageToPlayer(String s, String s1)
    {
        EntityPlayerMP entityplayermp = getPlayerEntity(s);
        if(entityplayermp != null)
        {
            entityplayermp.field_421_a.sendPacket(new Packet3Chat(s1));
        }
    }

    public void func_12022_a(double d, double d1, double d2, double d3, Packet packet)
    {
        for(int i = 0; i < playerEntities.size(); i++)
        {
            EntityPlayerMP entityplayermp = playerEntities.get(i);
            double d4 = d - entityplayermp.posX;
            double d5 = d1 - entityplayermp.posY;
            double d6 = d2 - entityplayermp.posZ;
            if(d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3)
            {
                entityplayermp.field_421_a.sendPacket(packet);
            }
        }

    }

    public void sendChatMessageToAllPlayers(String s)
    {
        Packet3Chat packet3chat = new Packet3Chat(s);
        for(int i = 0; i < playerEntities.size(); i++)
        {
            EntityPlayerMP entityplayermp = playerEntities.get(i);
            if(isOp(entityplayermp.username))
            {
                entityplayermp.field_421_a.sendPacket(packet3chat);
            }
        }

    }

    public boolean sendPacketToPlayer(String s, Packet packet)
    {
        EntityPlayerMP entityplayermp = getPlayerEntity(s);
        if(entityplayermp != null)
        {
            entityplayermp.field_421_a.sendPacket(packet);
            return true;
        } else
        {
            return false;
        }
    }

    public void sentTileEntityToPlayer(int i, int j, int k, TileEntity tileentity)
    {
        playerManagerObj.func_541_a(new Packet59ComplexEntity(i, j, k, tileentity), i, j, k);
    }

    public void savePlayerStates()
    {
        for(int i = 0; i < playerEntities.size(); i++)
        {
            playerNBTManagerObj.writePlayerData(playerEntities.get(i));
        }

    }

    public Set getWhiteListedPlayers() {
        return whiteListedPlayers;
    }

    public static Logger logger = Logger.getLogger("Minecraft");
    public List<EntityPlayerMP> playerEntities;
    private MinecraftServer mcServer;
    private PlayerManager playerManagerObj;
    public int maxPlayers;
    private Set bannedPlayers;
    private Set whiteListedPlayers;
    private Set bannedIPs;
    private Set ops;
    private File whiteListedPlayersFile;
    private File bannedPlayersFile;
    private File ipBanFile;
    private File opFile;
    private PlayerNBTManager playerNBTManagerObj;

}
