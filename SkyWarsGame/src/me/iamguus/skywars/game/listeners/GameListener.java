package me.iamguus.skywars.game.listeners;

import com.google.common.base.Function;
import me.iamguus.skywars.game.SkyWarsGame;
import me.iamguus.skywars.game.mechanics.GameData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Created by Guus2 on 06/07/2016.
 */
public class GameListener implements Listener {

    GameData gameData;
    Plugin plugin;

    public GameListener(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        gameData = SkyWarsGame.get().gameData;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (gameData.noFallDamage) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() != null) {
            if (player.getKiller() instanceof Player) {
                String template = ChatColor.GOLD + "%s " + ChatColor.BLUE + "was killed by " + ChatColor.GOLD + "%s! " + ChatColor.GRAY + "(%s/%s)";
                event.setDeathMessage(String.format(template, player.getName(), player.getKiller().getName(), gameData.players.size(), gameData.spawnLocations.size()));

                //TODO: Give XP and remove kit
            }
        }
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent lastDamage = (EntityDamageByEntityEvent) player.getLastDamageCause();
            if (lastDamage.getDamager() instanceof Projectile) {
                Projectile p = (Projectile) lastDamage.getDamager();
                if (p.getShooter() != null) {
                    if (p.getShooter() instanceof Player) {
                        gameData.players.remove(player);

                        String template = ChatColor.GOLD + "%s " + ChatColor.BLUE + "was killed by " + ChatColor.GOLD + "%s! " + ChatColor.GRAY + "(%s/%s)";
                        event.setDeathMessage(null);

                        Bukkit.broadcastMessage(String.format(template, player.getName(), ((Player) p.getShooter()).getName(), gameData.players.size(), gameData.spawnLocations.size()));
                        gameData.handleDeath(player, (Player) p.getShooter());
                    }
                }
            }
        } else {
            gameData.players.remove(player);

            String template = ChatColor.GOLD + "%s " + ChatColor.BLUE + "committed suicide! " + ChatColor.GRAY + "(%s/%s)";
            event.setDeathMessage(null);

            Bukkit.broadcastMessage(String.format(template, player.getName(), gameData.players.size(), gameData.spawnLocations.size()));
            gameData.handleDeath(player, null);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (gameData.state == GameData.GameState.INGAME) {
            Location spawnLocation = gameData.spawnLocations.entrySet().iterator().next().getKey();
            spawnLocation = spawnLocation.clone().add(0, 10, 0);

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(spawnLocation);
            gameData.spectators.add(player);
        } else {
            player.kickPlayer("Please reconnect!");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getY() <= 50) {
            if (event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                event.getPlayer().damage(20.0D, ((EntityDamageByEntityEvent) event.getPlayer().getLastDamageCause()).getDamager());
            } else {
                event.getPlayer().damage(20.0D);
                event.getPlayer().setLastDamageCause(new EntityDamageEvent(event.getPlayer(), EntityDamageEvent.DamageCause.VOID, new HashMap<>(), new HashMap<>()));
            }
        }
    }
}
