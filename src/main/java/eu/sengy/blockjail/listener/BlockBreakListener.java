package eu.sengy.blockjail.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static eu.sengy.blockjail.JailSpigot.blocks;
import static eu.sengy.blockjail.JailSpigot.connection;
import static org.bukkit.Bukkit.getServer;


public class BlockBreakListener implements Listener {

    private int currCount;
    private int neededCount;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){

        String playerUUID = e.getPlayer().getUniqueId().toString().replaceAll("-","");

        Material block = e.getBlock().getType();

        e.getBlock().getDrops().clear();

        if(block == Material.OBSIDIAN){
            blocks.add(e.getBlock().getLocation());
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT neededCount FROM jail WHERE uuid = ?"
                );
                statement.setString(1, playerUUID);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    neededCount = result.getInt("neededCount");
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }

            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT currCount FROM jail WHERE uuid = ?"
                );
                statement.setString(1, playerUUID);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    currCount = result.getInt("currCount");
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }

            currCount++;

            try {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE jail SET currCount = ? WHERE uuid = ?"
                );
                statement.setInt(1, currCount);
                statement.setString(2, playerUUID);
                statement.executeUpdate();


            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }


            if(currCount>=neededCount){
                getServer().dispatchCommand(getServer().getConsoleSender(),"sync bungee unjail " + e.getPlayer().getName());
            }
        }
    }

}
