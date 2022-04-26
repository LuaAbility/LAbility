package com.LAbility.LuaUtility;

import com.LAbility.LAbilityMain;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;

public class ResourcePackWebServer {
    public int port;
    private HttpServer httpServer;
    private BukkitTask task;

    public boolean start() {
        if (port < 0) return false;
        if (port == 0) port = 13356;
        stopTask();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    httpServer = Vertx.vertx().createHttpServer();
                    httpServer.requestHandler(httpServerRequest -> {
                        String url = httpServerRequest.uri();
                        url.replace("http://", "");

                        if (url.contains("/favicon.ico")) {
                            httpServerRequest.response().setStatusCode(401);
                            httpServerRequest.response().end();
                        } else {
                            if (!url.contains("/")) {
                                httpServerRequest.response().setStatusCode(401);
                                httpServerRequest.response().end();
                            } else {
                                String[] parts = url.split("/");
                                if (parts.length != 2) {
                                    httpServerRequest.response().setStatusCode(401);
                                    httpServerRequest.response().end();
                                } else {
                                    String uuid = parts[1];
                                    if (checkPlayerExist(uuid)) {
                                        httpServerRequest.response().sendFile(getFileLocation());
                                    } else {
                                        Bukkit.getConsoleSender().sendMessage("UUID Error (UUID : " + uuid + ")");
                                        httpServerRequest.response().setStatusCode(401);
                                        httpServerRequest.response().end();
                                    }
                                }
                            }
                        }
                    });
                    httpServer.listen(port);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(LAbilityMain.instance);
        return true;
    }

    public final String getWebIp() throws IOException {
        URL url = new URL("https://mcv.kr/myip/api.php");
        BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));

        String ip = bf.readLine();
        return "http://" + ip + ":" + port + "/";
    }

    public final String getFileLocation() {
        return LAbilityMain.instance.getDataFolder().getPath() + File.separator + "LAbilityResource.zip";
    }

    public final void stopTask() {
        if (task != null && !task.isCancelled()) task.cancel();
    }

    public boolean checkPlayerExist(String uuid){
        for (Player player : Bukkit.getOnlinePlayers()){
            if (Objects.equals(player.getUniqueId().toString(), uuid)) return true;
        }
        return false;
    }
}
