package me.iamguus.skywars.game.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Guus2 on 07/07/2016.
 */
public class MySQLHandler {

    Plugin plugin;

    public MySQLHandler(Plugin plugin) {
        this.plugin = plugin;
        connect();
    }

    Connection conn;

    public void connect() {
        try {
            String host = "127.0.0.1";
            String port = "3306";
            String user = "root";
            String pass = "alexander2002";
            String db = "server";

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, pass);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void createRow(Player player) {
        String sql = "INSERT INTO players (uuid, xp) VALUES(?, ?) ON DUPLICATE KEY UPDATE uuid=uuid;";


        try {
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, player.getUniqueId().toString());
            st.setInt(2, 0);

            st.executeUpdate();

            st.close();

            String sql2 = "INSERT INTO player_stats (id, uuid, kills, deaths, games_played, games_won) VALUES(?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE games_played = (games_played + 1);";
            st = conn.prepareStatement(sql2);

            st.setObject(1, null);
            st.setString(2, player.getUniqueId().toString());
            st.setInt(3, 0);
            st.setInt(4, 0);
            st.setInt(5, 0);
            st.setInt(6, 0);

            st.executeUpdate();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
