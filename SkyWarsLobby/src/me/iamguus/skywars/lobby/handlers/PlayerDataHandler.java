package me.iamguus.skywars.lobby.handlers;

import me.iamguus.skywars.lobby.SkyWarsLobby;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Guus2 on 08/07/2016.
 */
public class PlayerDataHandler {

    public Map<UUID, PlayerData> playerData;

    private SkyWarsLobby plugin;
    private MySQLHandler mysql;

    public PlayerDataHandler(SkyWarsLobby plugin) {
        this.plugin = plugin;
        playerData = new HashMap<>();
        mysql = plugin.mysql;
    }

    public PlayerData loadPlayerData(Player player) {
        String sql = "SELECT * FROM players WHERE uuid = ?;";

        try {
            PreparedStatement st = mysql.conn.prepareStatement(sql);

            st.setString(1, player.getUniqueId().toString());

            ResultSet rs = st.executeQuery();

            rs.next();

            int xp = rs.getInt("xp");

            return new PlayerData(player.getUniqueId(), xp);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public class PlayerData {

        private UUID playerUUID;
        private int xp;

        public PlayerData(UUID playerUUID, int xp) {
            this.playerUUID = playerUUID;
            this.xp = xp;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public int getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }
    }
}
