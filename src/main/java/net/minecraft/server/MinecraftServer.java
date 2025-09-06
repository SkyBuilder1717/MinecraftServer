// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.server;

import net.minecraft.src.*;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftServer
    implements ICommandListener, Runnable
{

    public MinecraftServer()
    {
        field_6025_n = true;
        field_6032_g = false;
        field_9014_h = 0;
        field_9010_p = new ArrayList<>();
        commands = Collections.synchronizedList(new ArrayList<>());
        new ThreadSleepForever(this);
    }

    private boolean func_6008_d() throws UnknownHostException
    {
        ThreadCommandReader threadcommandreader = new ThreadCommandReader(this);
        threadcommandreader.setDaemon(true);
        threadcommandreader.start();
        ConsoleLogManager.init();
        logger.info("Starting minecraft server version 0.1 (modified by SkyBuilder1717)");
        if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            logger.warning("**** NOT ENOUGH RAM!");
            logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        logger.info("Loading properties");
        propertyManagerObj = new PropertyManager(new File("server.properties"));
        String s = propertyManagerObj.getStringProperty("server-ip", "");
        onlineMode = propertyManagerObj.getBooleanProperty("online-mode", true);
        noAnimals = propertyManagerObj.getBooleanProperty("spawn-animals", true);
        field_9011_n = propertyManagerObj.getBooleanProperty("pvp", true);
        InetAddress inetaddress = null;
        if(!s.isEmpty())
        {
            inetaddress = InetAddress.getByName(s);
        }
        int i = propertyManagerObj.getIntProperty("server-port", 25565);
        logger.info("Starting Minecraft server on " + (!s.isEmpty() ? s : "*") + ":" + i);
        try
        {
            field_6036_c = new NetworkListenThread(this, inetaddress, i);
        }
        catch(IOException ioexception)
        {
            logger.warning("**** FAILED TO BIND TO PORT!");
            logger.log(Level.WARNING, "The exception was: " + ioexception);
            logger.warning("Perhaps a server is already running on that port?");
            return false;
        }
        if(!onlineMode)
        {
            logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            logger.warning("The server will make no attempt to authenticate usernames. Beware.");
            logger.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
        }
        configManager = new ServerConfigurationManager(this);
        field_6028_k = new EntityTracker(this);
        String s1 = propertyManagerObj.getStringProperty("level-name", "world");
        logger.info("Preparing level \"" + s1 + "\"");
        func_6017_c(s1);
        logger.info("Done! For help, type \"help\" or \"?\"");
        return true;
    }

    private void func_6017_c(String s)
    {
        logger.info("Preparing start region");
        worldMngr = new WorldServer(this, new File("."), s, propertyManagerObj.getBooleanProperty("hellworld", false) ? -1 : 0);
        worldMngr.func_4072_a(new WorldManager(this));
        worldMngr.monstersEnabled = propertyManagerObj.getBooleanProperty("spawn-monsters", true) ? 1 : 0;
        configManager.setPlayerManager(worldMngr);
        byte byte0 = 10;
        for(int i = -byte0; i <= byte0; i++)
        {
            func_6019_a(((i + byte0) * 100) / (byte0 + byte0 + 1));
            for(int j = -byte0; j <= byte0; j++)
            {
                if(!field_6025_n)
                {
                    return;
                }
                worldMngr.field_821.loadChunk((worldMngr.spawnX >> 4) + i, (worldMngr.spawnZ >> 4) + j);
            }

        }

        func_6011_e();
    }

    private void func_6019_a(int i)
    {
        field_9013_i = "Preparing spawn area";
        field_9012_j = i;
        System.out.println("Preparing spawn area" + ": " + i + "%");
    }

    private void func_6011_e()
    {
        field_9013_i = null;
        field_9012_j = 0;
    }

    private void saveServerWorld()
    {
        logger.info("Saving chunks");
        worldMngr.func_485_a(true, null);
    }

    private void func_6013_g()
    {
        logger.info("Stopping server");
        if(configManager != null)
        {
            configManager.savePlayerStates();
        }
        if(worldMngr != null)
        {
            saveServerWorld();
        }
    }

    public void func_6016_a()
    {
        field_6025_n = false;
    }

    public void run()
    {
        try
        {
            if(func_6008_d())
            {
                long l = System.currentTimeMillis();
                long l1 = 0L;
                while(field_6025_n) 
                {
                    long l2 = System.currentTimeMillis();
                    long l3 = l2 - l;
                    if(l3 > 2000L)
                    {
                        logger.warning("Can't keep up! Is the server overloaded? (" + l3 + " ms)");
                        l3 = 2000L;
                    }
                    if(l3 < 0L)
                    {
                        logger.warning("Time ran backwards! Did the system time change?");
                        l3 = 0L;
                    }
                    l1 += l3;
                    l = l2;
                    while(l1 > 50L) 
                    {
                        l1 -= 50L;
                        func_6018_h();
                    }
                    Thread.sleep(1L);
                }
            } else
            {
                while(field_6025_n) 
                {
                    commandLineParser();
                    try
                    {
                        Thread.sleep(10L);
                    }
                    catch(InterruptedException interruptedexception)
                    {
                        logger.warning("Error: " + interruptedexception.getMessage());
                    }
                }
            }
        }
        catch(Exception exception)
        {
            logger.warning("Error: " + exception.getMessage());
            logger.log(Level.SEVERE, "Unexpected exception", exception);
            while(field_6025_n) 
            {
                commandLineParser();
                try
                {
                    Thread.sleep(10L);
                }
                catch(InterruptedException interruptedexception1)
                {
                    logger.warning("Error: " + interruptedexception1.getMessage());
                }
            }
        }
        finally
        {
            func_6013_g();
            field_6032_g = true;
            System.exit(0);
        }
    }

    private void func_6018_h()
    {
        ArrayList<Object> arraylist = new ArrayList<>();
        for (Object o : field_6037_b.keySet()) {
            String s = (String) o;
            int k = (Integer) field_6037_b.get(s);
            if (k > 0) {
                field_6037_b.put(s, k - 1);
            } else {
                arraylist.add(s);
            }
        }

        for (Object o : arraylist) {
            field_6037_b.remove(o);
        }

        AxisAlignedBB.clearBoundingBoxPool();
        Vec3D.initialize();
        field_9014_h++;
        if(field_9014_h % 20 == 0)
        {
            configManager.sendPacketToAllPlayers(new Packet4UpdateTime(worldMngr.worldTime));
        }
        worldMngr.tick();
        while(worldMngr.func_6156_d()) ;
        worldMngr.func_459_b();
        field_6036_c.func_715_a();
        configManager.func_637_b();
        field_6028_k.func_607_a();
        for (Object o : field_9010_p) {
            ((IUpdatePlayerListBox) o).update();
        }

        try
        {
            commandLineParser();
        }
        catch(Exception exception)
        {
            logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception);
        }
    }

    public void addCommand(String s, ICommandListener icommandlistener)
    {
        commands.add(new ServerCommand(s, icommandlistener));
    }

    public void commandLineParser()
    {
        if (!commands.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) commands.remove(0);
            String s = servercommand.command;
            ICommandListener icommandlistener = servercommand.commandListener;
            String s1 = icommandlistener.getUsername();
            if (s.toLowerCase().startsWith("help") || s.toLowerCase().startsWith("?")) {
                icommandlistener.log("To run the server without a gui, start it like this:");
                icommandlistener.log("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
                icommandlistener.log("Console commands:");
                icommandlistener.log("   help  or  ?               shows this message");
                icommandlistener.log("   kick <player>             removes a player from the server");
                icommandlistener.log("   ban <player>              bans a player from the server");
                icommandlistener.log("   pardon <player>           pardons a banned player so that they can connect again");
                icommandlistener.log("   ban-ip <ip>               bans an IP address from the server");
                icommandlistener.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
                icommandlistener.log("   op <player>               turns a player into an op");
                icommandlistener.log("   deop <player>             removes op status from a player");
                icommandlistener.log("   tp [player] <x, y, z>     moves one player to the same location as another player");
                icommandlistener.log("   give <player> <id> [num]  gives a player a resource");
                icommandlistener.log("   time [num]                tells a player the time or sets it");
                icommandlistener.log("   tell <player> <message>   sends a private message to a player");
                icommandlistener.log("   stop                      gracefully stops the server");
                icommandlistener.log("   save-all                  forces a server-wide level save");
                icommandlistener.log("   save-off                  disables terrain saving (useful for backup scripts)");
                icommandlistener.log("   save-on                   re-enables terrain saving");
                icommandlistener.log("   list                      lists all currently connected players");
                icommandlistener.log("   say <message>             broadcasts a message to all players");
            } else if (s.toLowerCase().startsWith("list")) {
                icommandlistener.log("Connected players: " + configManager.getPlayerList());
            } else if (s.toLowerCase().startsWith("time")) {
                String[] parts = s.split(" ");
                World world = worldMngr;
                long ticks = world.worldTime % 24000;
                int totalMinutes = (int) (ticks * 60 / 1000);
                int hours = (6 + totalMinutes / 60) % 24;
                int minutes = totalMinutes % 60;

                if (parts.length == 1) {
                    icommandlistener.log(String.format("Current time: %02d:%02d", hours, minutes));
                } else {
                    String arg = parts[1];
                    try {
                        if (arg.contains(":")) {
                            String[] hm = arg.split(":");
                            int h = Integer.parseInt(hm[0]);
                            int m = Integer.parseInt(hm[1]);
                            world.worldTime = ((h - 6 + 24) % 24) * 1000 + (m * 1000L / 60);
                            icommandlistener.log("Time set to: " + String.format("%02d:%02d", h, m));
                        } else {
                            long time = Long.parseLong(arg);
                            world.worldTime = time;
                            icommandlistener.log("Time set to: " + time);
                        }
                    } catch (NumberFormatException e) {
                        icommandlistener.log("Invalid number or format! Use HH:MM or ticks.");
                    }
                }
            }
            if (s.toLowerCase().startsWith("stop")) {
                func_6014_a(s1, "Stopping the server..");
                field_6025_n = false;
            } else if (s.toLowerCase().startsWith("save-all")) {
                func_6014_a(s1, "Forcing save..");
                worldMngr.func_485_a(true, null);
                func_6014_a(s1, "Save complete.");
            } else if (s.toLowerCase().startsWith("save-off")) {
                func_6014_a(s1, "Disabling level saving..");
                worldMngr.field_816_A = true;
            } else if (s.toLowerCase().startsWith("save-on")) {
                func_6014_a(s1, "Enabling level saving..");
                worldMngr.field_816_A = false;
            } else if (s.toLowerCase().startsWith("op ")) {
                String s2 = s.substring(s.indexOf(" ")).trim();
                configManager.opPlayer(s2);
                func_6014_a(s1, "Opping " + s2);
                configManager.sendChatMessageToPlayer(s2, "\247eYou are now op!");
            } else if (s.toLowerCase().startsWith("deop ")) {
                String s3 = s.substring(s.indexOf(" ")).trim();
                configManager.deopPlayer(s3);
                configManager.sendChatMessageToPlayer(s3, "\247eYou are no longer op!");
                func_6014_a(s1, "De-opping " + s3);
            } else if (s.toLowerCase().startsWith("ban-ip ")) {
                String s4 = s.substring(s.indexOf(" ")).trim();
                configManager.banIP(s4);
                func_6014_a(s1, "Banning ip " + s4);
            } else if (s.toLowerCase().startsWith("pardon-ip ")) {
                String s5 = s.substring(s.indexOf(" ")).trim();
                configManager.unbanIP(s5);
                func_6014_a(s1, "Pardoning ip " + s5);
            } else if (s.toLowerCase().startsWith("ban ")) {
                String s6 = s.substring(s.indexOf(" ")).trim();
                configManager.banPlayer(s6);
                func_6014_a(s1, "Banning " + s6);
                EntityPlayerMP entityplayermp = configManager.getPlayerEntity(s6);
                if (entityplayermp != null) {
                    entityplayermp.field_421_a.func_43_c("Banned by admin");
                }
            } else if (s.toLowerCase().startsWith("pardon ")) {
                String s7 = s.substring(s.indexOf(" ")).trim();
                configManager.unbanPlayer(s7);
                func_6014_a(s1, "Pardoning " + s7);
            } else if (s.toLowerCase().startsWith("kick ")) {
                String s8 = s.substring(s.indexOf(" ")).trim();
                EntityPlayerMP entityplayermp1 = null;
                for (int i = 0; i < configManager.playerEntities.size(); i++) {
                    EntityPlayerMP entityplayermp5 = (EntityPlayerMP) configManager.playerEntities.get(i);
                    if (entityplayermp5.username.equalsIgnoreCase(s8)) {
                        entityplayermp1 = entityplayermp5;
                    }
                }

                if (entityplayermp1 != null) {
                    entityplayermp1.field_421_a.func_43_c("Kicked by admin");
                    func_6014_a(s1, "Kicking " + entityplayermp1.username);
                } else {
                    icommandlistener.log("Can't find user " + s8 + ". No kick.");
                }
            } else if (s.toLowerCase().startsWith("tp ")) {
                String[] as = s.split(" ");
                if (as.length == 3) {
                    EntityPlayerMP from = configManager.getPlayerEntity(as[1]);
                    EntityPlayerMP to = configManager.getPlayerEntity(as[2]);
                    if (from == null) {
                        icommandlistener.log("Can't find user " + as[1] + ". No tp.");
                    } else if (to == null) {
                        icommandlistener.log("Can't find user " + as[2] + ". No tp.");
                    } else {
                        from.field_421_a.func_41_a(
                                to.posX, to.posY, to.posZ,
                                to.rotationYaw, to.rotationPitch
                        );
                        func_6014_a(s1, "Teleporting " + as[1] + " to " + as[2] + ".");
                    }
                }
                else if (as.length == 5) {
                    EntityPlayerMP player = configManager.getPlayerEntity(as[1]);
                    if (player == null) {
                        icommandlistener.log("Can't find user " + as[1] + ". No tp.");
                    } else {
                        try {
                            double x = Double.parseDouble(as[2]);
                            double y = Double.parseDouble(as[3]);
                            double z = Double.parseDouble(as[4]);
                            player.field_421_a.func_41_a(
                                    x, y, z,
                                    player.rotationYaw, player.rotationPitch
                            );
                            func_6014_a(s1, "Teleporting " + as[1] + " to " + x + ", " + y + ", " + z + ".");
                        } catch (NumberFormatException e) {
                            icommandlistener.log("Invalid coordinates!");
                        }
                    }
                }
                else if (as.length == 4) {
                    EntityPlayerMP player = configManager.getPlayerEntity(s1);
                    if (player == null) {
                        icommandlistener.log("Can't find user " + s1 + ". No tp.");
                    } else {
                        try {
                            double x = Double.parseDouble(as[1]);
                            double y = Double.parseDouble(as[2]);
                            double z = Double.parseDouble(as[3]);
                            player.field_421_a.func_41_a(
                                    x, y, z,
                                    player.rotationYaw, player.rotationPitch
                            );
                            func_6014_a(s1, "Teleporting " + s1 + " to " + x + ", " + y + ", " + z + ".");
                        } catch (NumberFormatException e) {
                            icommandlistener.log("Invalid coordinates!");
                        }
                    }
                } else {
                    icommandlistener.log("Syntax error, use: /tp <player1> <player2> OR /tp <player> <x> <y> <z> OR /tp <x> <y> <z>");
                }
            } else if (s.toLowerCase().startsWith("give ")) {
                String[] as1 = s.split(" ");
                if (as1.length != 3 && as1.length != 4) {
                    return;
                }
                String s9 = as1[1];
                EntityPlayerMP entityplayermp4 = configManager.getPlayerEntity(s9);
                if (entityplayermp4 != null) {
                    try {
                        int j = Integer.parseInt(as1[2]);
                        if (Item.itemsList[j] != null) {
                            func_6014_a(s1, "Giving " + entityplayermp4.username + " some " + j);
                            int k = 1;
                            if (as1.length > 3) {
                                k = func_6020_b(as1[3]);
                            }
                            if (k < 1) {
                                k = 1;
                            }
                            if (k > 64) {
                                k = 64;
                            }
                            entityplayermp4.func_161_a(new ItemStack(j, k));
                        } else {
                            icommandlistener.log("There's no item with id " + j);
                        }
                    } catch (NumberFormatException numberformatexception) {
                        icommandlistener.log("There's no item with id " + as1[2]);
                    }
                } else {
                    icommandlistener.log("Can't find user " + s9);
                }
            } else if (s.toLowerCase().startsWith("say ")) {
                s = s.substring(s.indexOf(" ")).trim();
                logger.info("[" + s1 + "] " + s);
                configManager.sendPacketToAllPlayers(new Packet3Chat("\247d[Server] " + s));
            } else if (s.toLowerCase().startsWith("tell ")) {
                String[] as2 = s.split(" ");
                if (as2.length >= 3) {
                    s = s.substring(s.indexOf(" ")).trim();
                    s = s.substring(s.indexOf(" ")).trim();
                    logger.info("[" + s1 + "->" + as2[1] + "] " + s);
                    s = "\2477" + s1 + " whispers " + s;
                    logger.info(s);
                    if (!configManager.sendPacketToPlayer(as2[1], new Packet3Chat(s))) {
                        icommandlistener.log("There's no player by that name online.");
                    }
                }
            } else {
                logger.info("Unknown console command: \"" + s.toLowerCase() + "\". Type \"help\" for help.");
            }
        }
    }

    private void func_6014_a(String s, String s1)
    {
        String s2 = s + ": " + s1;
        configManager.sendChatMessageToAllPlayers("\2477(" + s2 + ")");
        logger.info(s2);
    }

    private int func_6020_b(String s)
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch(NumberFormatException numberformatexception)
        {
            return 1;
        }
    }

    public void func_6022_a(IUpdatePlayerListBox iupdateplayerlistbox)
    {
        field_9010_p.add(iupdateplayerlistbox);
    }

    public static void main(String[] args)
    {
        try
        {
            MinecraftServer minecraftserver = new MinecraftServer();
            if(!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui")))
            {
                ServerGUI.initGui(minecraftserver);
            }
            (new ThreadServerApplication("Server thread", minecraftserver)).start();
        }
        catch(Exception exception)
        {
            logger.log(Level.SEVERE, "Failed to start the minecraft server", exception);
        }
    }

    public File getFile(String s)
    {
        return new File(s);
    }

    public void log(String s)
    {
        logger.info(s);
    }

    public String getUsername()
    {
        return "CONSOLE";
    }

    public static boolean func_6015_a(MinecraftServer minecraftserver)
    {
        return minecraftserver.field_6025_n;
    }

    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap<Object, Object> field_6037_b = new HashMap<>();
    public NetworkListenThread field_6036_c;
    public PropertyManager propertyManagerObj;
    public WorldServer worldMngr;
    public ServerConfigurationManager configManager;
    private boolean field_6025_n;
    public boolean field_6032_g;
    int field_9014_h;
    public String field_9013_i;
    public int field_9012_j;
    private final List field_9010_p;
    private final List commands;
    public EntityTracker field_6028_k;
    public boolean onlineMode;
    public boolean noAnimals;
    public boolean field_9011_n;
}
