package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.*;

class ThreadLoginVerifier extends Thread
{

    ThreadLoginVerifier(NetLoginHandler netloginhandler, Packet1Login packet1login)
    {
        loginHandler = netloginhandler;
        loginPacket = packet1login;
    }

    public void run() {
        try {
            String serverId = NetLoginHandler.getServerId(loginHandler);
            String username = loginPacket.username;

            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/hasJoined?username="
                    + URLEncoder.encode(username, "UTF-8")
                    + "&serverId=" + URLEncoder.encode(serverId, "UTF-8"));

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String response = reader.readLine();
            reader.close();

            if (response != null && !response.trim().isEmpty()) {
                NetLoginHandler.setLoginPacket(loginHandler, loginPacket);
                for (EventListener l : loginHandler.mcServer.pluginManager.getListeners()) {
                    l.onPlayerLogin(loginPacket.username);
                }
            } else {
                loginHandler.kickUser("Failed to verify username!");
            }
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
            loginHandler.kickUser("Verification error: " + exception.getMessage());
        }
    }

    final Packet1Login loginPacket; /* synthetic field */
    final NetLoginHandler loginHandler; /* synthetic field */
}
