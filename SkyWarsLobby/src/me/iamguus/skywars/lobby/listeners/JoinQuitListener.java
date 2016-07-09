package me.iamguus.skywars.lobby.listeners;

import me.iamguus.skywars.lobby.SkyWarsLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Guus2 on 08/07/2016.
 */
public class JoinQuitListener implements Listener {

    private SkyWarsLobby plugin;

    public JoinQuitListener(SkyWarsLobby plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.playerData.playerData.put(player.getUniqueId(), plugin.playerData.loadPlayerData(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.playerData.playerData.containsKey(player.getUniqueId())) {
            plugin.playerData.playerData.remove(player.getUniqueId());
        }
    }
}
