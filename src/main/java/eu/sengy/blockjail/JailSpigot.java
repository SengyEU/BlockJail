package eu.sengy.blockjail;

import eu.sengy.blockjail.listener.BlockBreakListener;
import eu.sengy.blockjail.listener.JoinListener;
import eu.sengy.blockjail.listener.LeaveListener;
import eu.sengy.blockjail.listener.PluginMessage;
import eu.sengy.blockjail.utils.Papi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JailSpigot extends JavaPlugin {

    private static JailSpigot instance;

    public static JailSpigot getInstance(){
        return instance;
    }

    private static void setInstance(JailSpigot instance){
        JailSpigot.instance = instance;
    }

    public static Connection connection;

    public static List<Location> blocks = new ArrayList<>();

    public static List<Player> onlinePlayers = new ArrayList<>();

    String host = "";
    String database = "";
    String username = "";
    String password = "";
    String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false&autoReconnect=true";

    @Override
    public void onEnable(){
        setInstance(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this,"BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this,"BungeeCord", new PluginMessage());
        getServer().getPluginManager().registerEvents(new BlockBreakListener(),this);
        getServer().getPluginManager().registerEvents(new JoinListener(),this);
        getServer().getPluginManager().registerEvents(new LeaveListener(),this);
        new Papi().register();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(JailSpigot.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Location block : blocks){
                    block.getBlock().setType(Material.OBSIDIAN);
                }
                getServer().dispatchCommand(getServer().getConsoleSender(),"broadcast &fBlocky respawnuty");
                getServer().dispatchCommand(getServer().getConsoleSender(),"stimer set 00:00:59 stola");
                Location spawn = new Location(getServer().getWorld("world"), 0.5, 85, 0.5, 0, 0);
                for(Player player: getServer().getOnlinePlayers()){
                    player.teleport(spawn);
                }
                blocks.clear();
            }
        }, 0, 20 * 60);


        try {
            connection = DriverManager.getConnection(url, username, password);
            getLogger().info("Connected to MySQL database.");
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to MySQL database: " + e.getMessage());
        }

    }

    @Override
    public void onDisable(){

        try {
            Field field = JavaPlugin.class.getDeclaredField("isEnabled");
            field.setAccessible(true);
            field.set(this, true);

            for (Player player : onlinePlayers) {
                new PluginMessage().kickPlayer(player, "Jail restarted.");
            }
            getServer().dispatchCommand(getServer().getConsoleSender(),"stimer remove stola");

            field.set(this, false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
