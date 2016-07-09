package me.iamguus.skywars.lobby;

import me.iamguus.skywars.lobby.gui.KitGUI;
import me.iamguus.skywars.lobby.handlers.KitHandler;
import me.iamguus.skywars.lobby.handlers.MySQLHandler;
import me.iamguus.skywars.lobby.handlers.PlayerDataHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Guus2 on 07/07/2016.
 */
public class SkyWarsLobby extends JavaPlugin {

    private static SkyWarsLobby plugin;

    public MySQLHandler mysql;
    public KitHandler kits;
    public PlayerDataHandler playerData;
    public KitGUI kitsGUI;

    public void onEnable() {
        plugin = this;

        mysql = new MySQLHandler(plugin);
        kits = new KitHandler(plugin);
        playerData = new PlayerDataHandler(plugin);
        kitsGUI = new KitGUI(plugin);
    }

    public void onDisable() {
        plugin = null;
    }
}
