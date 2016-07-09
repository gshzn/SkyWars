package me.iamguus.skywars.lobby.gui;

import me.iamguus.skywars.lobby.SkyWarsLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by Guus2 on 08/07/2016.
 */
public class KitGUI {

    private SkyWarsLobby plugin;

    public KitGUI(SkyWarsLobby plugin) {
        this.plugin = plugin;
    }

    public Inventory getMainInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, 27, "Your kits");


        return inv;
    }
}
