package eu.sengy.blockjail;

import eu.sengy.blockjail.commands.*;
import eu.sengy.blockjail.listener.ServerConnectListener;
import eu.sengy.blockjail.utils.Config;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class JailBungee extends Plugin {

    private static JailBungee instance;

    public static JailBungee getInstance(){
        return instance;
    }

    private static void setInstance(JailBungee instance){
        JailBungee.instance = instance;
    }



    public static Connection connection;

    public static List<String> jailedPlayers = new ArrayList<>();

    public static String host = "";
    public static String database = "";
    public static String username = "";
    public static String password = "";
    public static String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false&autoReconnect=true";


    @Override
    public void onEnable() {

        setInstance(this);

        Config.load();

        getProxy().getPluginManager().registerListener(this, new ServerConnectListener());
        getProxy().getPluginManager().registerCommand(this, new Jail());
        getProxy().getPluginManager().registerCommand(this, new JailReload());
        getProxy().getPluginManager().registerCommand(this, new Unjail());
        getProxy().getPluginManager().registerCommand(this, new JailHistory());
        getProxy().getPluginManager().registerCommand(this, new JailActive());

        try {
            connection = DriverManager.getConnection(url, username, password);
            getLogger().info("Connected to MySQL database.");
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to MySQL database: " + e.getMessage());
        }

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid FROM jail"
            );
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                jailedPlayers.add(result.getString("uuid"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            connection.close();
            getLogger().info("Disconnected from MySQL database.");
        } catch (SQLException e) {
            getLogger().severe("Failed to disconnect from MySQL database: " + e.getMessage());
        }
    }

}
