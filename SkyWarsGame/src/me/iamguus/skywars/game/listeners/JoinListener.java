package me.iamguus.skywars.game.listeners;

import me.iamguus.skywars.game.SkyWarsGame;
import me.iamguus.skywars.game.mechanics.GameData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * Created by Guus on 5-7-2016.
 */
public class JoinListener implements Listener {

    GameData gameData;
    Plugin plugin;

    public JoinListener(Plugin plugin) {
        this.plugin = plugin;
        gameData = SkyWarsGame.get().gameData;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        SkyWarsGame.get().mysql.createRow(event.getPlayer());
        if (gameData.state == GameData.GameState.WAITING) {
            Location spawnLocation = gameData.findOpenSpot();
            if (spawnLocation != null) {
                event.getPlayer().teleport(spawnLocation.clone().add(0.5, 0, 0.5));
                event.setJoinMessage(String.format(ChatColor.BLUE + event.getPlayer().getName() + ChatColor.GRAY + " joined the game! (%s/%s)", Bukkit.getOnlinePlayers().size(), gameData.spawnLocations.size()));
                gameData.players.add(event.getPlayer());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                gameData.checkForStart();
            } else {
                event.getPlayer().kickPlayer("The game is full!");
            }
        } else {
            event.getPlayer().kickPlayer("The game has already begun!");
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (gameData.state == GameData.GameState.INGAME) {
            String template = ChatColor.GOLD + "%s" + ChatColor.BLUE + " committed suicide! " + ChatColor.GRAY + "(%s/%s)";
            gameData.players.remove(player);
            event.setQuitMessage(String.format(template, player.getName(), gameData.players.size(), gameData.spawnLocations.size()));
            gameData.handleDeath(player, null);

            for (Map.Entry<Location, Boolean> entry : gameData.spawnLocations.entrySet()) {
                if (player.getLocation().getBlockX() ==  entry.getKey().getBlockX() &&
                        player.getLocation().getBlockY() == entry.getKey().getBlockY() &&
                        player.getLocation().getBlockZ() == entry.getKey().getBlockZ()) {
                    entry.setValue(false);
                }
            }
        } else {
            String template = ChatColor.GOLD + "%s" + ChatColor.BLUE + " left the game! " + ChatColor.GRAY + "(%s/%s)";
            gameData.players.remove(player);
            event.setQuitMessage(String.format(template, player.getName(), gameData.players.size(), gameData.spawnLocations.size()));
        }
    }

}
