package me.iamguus.skywars.lobby.handlers;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
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
}
