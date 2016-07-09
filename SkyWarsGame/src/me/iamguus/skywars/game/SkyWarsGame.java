package me.iamguus.skywars.game;

import me.iamguus.skywars.game.listeners.GameListener;
import me.iamguus.skywars.game.listeners.JoinListener;
import me.iamguus.skywars.game.mechanics.GameData;
import me.iamguus.skywars.game.util.MySQLHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Guus on 5-7-2016.
 */
public class SkyWarsGame extends JavaPlugin {

    private static SkyWarsGame game;

    public GameData gameData;
    public MySQLHandler mysql;

    public void onEnable() {
        game = this;

        gameData = new GameData(game);

        gameData.createMap(gameData.initialize());

        mysql = new MySQLHandler(this);

        new JoinListener(this);
        new GameListener(this);
    }

    public static SkyWarsGame get() {
        return game;
    }
}
