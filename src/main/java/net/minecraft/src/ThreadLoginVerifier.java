package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.*;
import java.net.URL;
import net.minecraft.server.*;

class ThreadLoginVerifier extends Thread
{

    ThreadLoginVerifier(NetLoginHandler netloginhandler, Packet1Login packet1login)
    {
        loginHandler = netloginhandler;
        loginPacket = packet1login;
    }

    public void run()
    {
        try
        {
            String s = NetLoginHandler.getServerId(loginHandler);
            URL url = new URL("https://session.minecraft.net/game/checkserver.jsp?user=" + loginPacket.username + "&serverId=" + s);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s1 = bufferedreader.readLine();
            bufferedreader.close();
            if(s1.equals("YES"))
            {
                NetLoginHandler.setLoginPacket(loginHandler, loginPacket);
                for (EventListener l : loginHandler.mcServer.pluginManager.getListeners()) {
                    l.onPlayerLogin(loginPacket.username);
                }
            } else
            {
                loginHandler.kickUser("Failed to verify username!");
            }
        }
        catch(Exception exception)
        {
            ExceptionLogger.log(exception);
        }
    }

    final Packet1Login loginPacket; /* synthetic field */
    final NetLoginHandler loginHandler; /* synthetic field */
}
