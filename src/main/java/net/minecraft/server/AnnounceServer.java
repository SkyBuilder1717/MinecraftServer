package net.minecraft.server;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AnnounceServer {
    private static long lastReport = 0;

    public static void tick(String motd, int onlinePlayers, int maxPlayers, String address) {
        long now = System.currentTimeMillis();
        if (now - lastReport < 60000) {
            return;
        }
        lastReport = now;

        try {
            String urlStr = "https://skybuilder.synology.me/skyalphalauncher/api/v1/serverslist/?action=add"
                    + "&motd=" + URLEncoder.encode(motd, "UTF-8")
                    + "&players=" + URLEncoder.encode(onlinePlayers + "/" + maxPlayers, "UTF-8")
                    + "&address=" + URLEncoder.encode(address, "UTF-8");

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.getInputStream().close();
        } catch (Exception e) {
            System.out.println("[AnnounceServer] Failed to send server info: " + e.getMessage());
        }
    }
}

