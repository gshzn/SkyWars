package me.iamguus.skywars.game.mechanics;

import fr.rhaz.socketapi.SocketAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import fr.rhaz.socketapi.Bukkit.BukkitSocketHandshakeEvent;

/**
 * Created by Guus on 5-7-2016.
 */
public class StateChanger implements Listener {

    private Plugin plugin;

    private HashMap<String, SocketAPI.Server.SocketMessenger> sockets;

    public StateChanger(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBukkitHandshake(BukkitSocketHandshakeEvent event) {

    }

//    public void sendStatusChange()

}
