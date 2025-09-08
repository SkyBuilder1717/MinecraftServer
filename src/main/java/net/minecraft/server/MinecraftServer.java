// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package net.minecraft.server;

import net.minecraft.src.*;
import net.minecraft.server.commands.*;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftServer
    implements ICommandListener, Runnable {

    public MinecraftServer()
    {
        instance = this;
        server_working = true;
        field_6032_g = false;
        ticks = 0;
        field_9010_p = new ArrayList<>();
        pluginManager = new PluginManager();
        new ThreadSleepForever(this);

        ServerCommands.register("help", new HelpCommand());
        ServerCommands.register("heal", new HealCommand());
        ServerCommands.register("kill", new KillCommand());
        ServerCommands.register("give", new GiveCommand());
        ServerCommands.register("tp", new TpCommand());
        ServerCommands.register("tell", new TellCommand());
        ServerCommands.register("time", new TimeCommand());
        ServerCommands.register("ban", new BanCommand());
        ServerCommands.register("ban-ip", new BanIpCommand());
        ServerCommands.register("pardon", new PardonCommand());
        ServerCommands.register("pardon-ip", new PardonIpCommand());
        ServerCommands.register("vanish", new VanishCommand());
        ServerCommands.register("save-all", new SaveAllCommand());
        ServerCommands.register("save-on", new SaveOnCommand());
        ServerCommands.register("save-off", new SaveOffCommand());
        ServerCommands.register("kick", new KickCommand());
        ServerCommands.register("stop", new StopCommand());
        ServerCommands.register("summon", new SummonCommand());
        ServerCommands.register("op", new OpCommand());
        ServerCommands.register("deop", new DeopCommand());
        ServerCommands.register("say", new SayCommand());
        ServerCommands.register("plugins", new PluginsCommand());

    }

    private boolean host_started() throws UnknownHostException
    {
        ThreadCommandReader threadcommandreader = new ThreadCommandReader(this);
        threadcommandreader.setDaemon(true);
        threadcommandreader.start();
        ConsoleLogManager.init();
        logger.info("Starting minecraft server version SkyAlphaServer v0.1");
        if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            logger.warning("**** NOT ENOUGH RAM!");
            logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        logger.info("Loading properties");
        propertyManagerObj = new PropertyManager(new File("server.properties"));
        String s = propertyManagerObj.getStringProperty("server-ip", "");
        onlineMode = propertyManagerObj.getBooleanProperty("online-mode", true);
        spawnProtection = propertyManagerObj.getIntProperty("spawn-protection", 16);
        noAnimals = propertyManagerObj.getBooleanProperty("spawn-animals", true);
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
        EntityRegistryInitializer.init();
        initWorld(s1);
        pluginManager.loadPlugins(this);
        logger.info("Done! For help, type \"help\"");
        return true;
    }

    private void initWorld(String s)
    {
        logger.info("Preparing start region");
        worldMngr = new WorldServer(this, new File("."), s, propertyManagerObj.getBooleanProperty("hellworld", false) ? -1 : 0);
        worldMngr.func_4072_a(new WorldManager(this));
        worldMngr.monstersEnabled = propertyManagerObj.getBooleanProperty("spawn-monsters", true) ? 1 : 0;
        worldMngr.pvpEnabled = propertyManagerObj.getBooleanProperty("pvp", true);
        configManager.setPlayerManager(worldMngr);
        byte byte0 = 10;
        for(int i = -byte0; i <= byte0; i++)
        {
            prepareArea(((i + byte0) * 100) / (byte0 + byte0 + 1));
            for(int j = -byte0; j <= byte0; j++)
            {
                if(!server_working)
                {
                    return;
                }
                worldMngr.field_821.loadChunk((worldMngr.spawnX >> 4) + i, (worldMngr.spawnZ >> 4) + j);
            }

        }

        done();
    }

    private void prepareArea(int i)
    {
        loadingMessage = "Preparing spawn area";
        generationPercentage = i;
        System.out.println("Preparing spawn area" + ": " + i + "%");
    }

    private void done()
    {
        loadingMessage = null;
        generationPercentage = 0;
    }

    public void func_6016_a() {
        pluginManager.disablePlugins(this);
        logger.info("Stopping server");

        if(configManager != null) {
            configManager.sendPacketToAllPlayers(new Packet3Chat("Server shutting down"));
            for (Object obj : configManager.playerEntities) {
                EntityPlayerMP player = (EntityPlayerMP) obj;
                if(player != null && player.field_421_a != null) {
                    player.field_421_a.sendPacket(new Packet255KickDisconnect("Server shutting down"));
                }
            }
            configManager.savePlayerStates();
        }

        field_6032_g = true;

        if(worldMngr != null) {
            try {
                logger.info("Saving chunks");
                worldMngr.func_485_a(true, null);
            } catch(Exception e) {
                logger.warning("Failed to save world: " + e.getMessage());
            }
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            logger.warning("Interrupted while waiting for save: " + e.getMessage());
        }

        server_working = false;
    }

    public void run() {
        try {
            boolean started = host_started();

            if (!started) {
                logger.severe("Server failed to start!");
                server_working = false;
                return;
            }

            long lastTime = System.currentTimeMillis();
            long accumulated = 0L;

            while (server_working) {
                long now = System.currentTimeMillis();
                long delta = now - lastTime;

                if (delta > 2000L) {
                    logger.warning("Can't keep up! Is the server overloaded? (" + delta + " ms)");
                    delta = 2000L;
                }
                if (delta < 0L) delta = 0L;

                accumulated += delta;
                lastTime = now;

                while (accumulated > 50L) {
                    accumulated -= 50L;
                    tickServer();
                }

                Thread.sleep(1L);
            }
        } catch (Exception e) {
            ExceptionLogger.log(e);
        } finally {
            logger.info("Server thread exiting.");
            System.exit(0);
        }
    }

    private void tickServer() {
        if (field_6032_g) return;
        ArrayList<Object> arraylist = new ArrayList<>();
        for (Object o : counters.keySet()) {
            String s = (String) o;
            int k = (Integer) counters.get(s);
            if (k > 0) {
                counters.put(s, k - 1);
            } else {
                arraylist.add(s);
            }
        }
        for (Object o : arraylist) {
            counters.remove(o);
        }
        AxisAlignedBB.clearBoundingBoxPool();
        Vec3D.initialize();
        ticks++;
        if (ticks % 20 == 0) {
            configManager.sendPacketToAllPlayers(
                    new Packet4UpdateTime(worldMngr.worldTime)
            );
        }
        worldMngr.tick();
        while(worldMngr.func_6156_d()) ;
        worldMngr.func_459_b();
        field_6036_c.func_715_a();
        configManager.func_637_b();
        field_6028_k.func_607_a();
        for (IUpdatePlayerListBox o : field_9010_p) {
            o.update();
        }
        for (EventListener l : pluginManager.getListeners()) {
            l.onServerTick(this);
        }
    }

    public void addCommand(String s, ICommandListener mcServer) {
        ServerCommand servercommand = new ServerCommand(s, mcServer);
        String cmd = servercommand.command.trim();
        ICommandListener icommandlistener = servercommand.commandListener;

        String[] args = cmd.split(" ");
        String command = args[0].toLowerCase();
        ICommand entry = ServerCommands.getCommand(command);
        if (entry != null) {
            if (!entry.OnlyOP() || (entry.OnlyOP() && configManager.isOp(mcServer.getUsername()))) {
                String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
                entry.execute(this, icommandlistener, realArgs);
                for (EventListener l : pluginManager.getListeners()) {
                    l.onPlayerCommand(icommandlistener, command, args);
                }
                return;
            }
        }

        icommandlistener.log("Unknown console command: \"" + cmd.toLowerCase() + "\".");
        icommandlistener.log("Type \"help\" for help.");
    }

    public void send_message(String s, String s1)
    {
        String s2 = s + ": " + s1;
        configManager.sendChatMessageToAllPlayers("\2477(" + s2 + ")");
        logger.info(s2);
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
        return minecraftserver.server_working;
    }

    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap<Object, Object> counters = new HashMap<>();
    public NetworkListenThread field_6036_c;
    public PropertyManager propertyManagerObj;
    public WorldServer worldMngr;
    public ServerConfigurationManager configManager;
    public boolean server_working;
    public boolean field_6032_g;
    public PluginManager pluginManager;
    public int spawnProtection;
    int ticks;
    public String loadingMessage;
    public int generationPercentage;
    private final List<IUpdatePlayerListBox> field_9010_p;
    public EntityTracker field_6028_k;
    public boolean onlineMode;
    public boolean noAnimals;
    public final HashMap<EntityPlayerMP, Boolean> vanishedPlayers = new HashMap<>();
    public static MinecraftServer instance;
}
