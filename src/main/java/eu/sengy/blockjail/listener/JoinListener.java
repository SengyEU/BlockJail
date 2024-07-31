package eu.sengy.blockjail.listener;

import eu.sengy.blockjail.utils.MMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static eu.sengy.blockjail.JailSpigot.connection;
import static eu.sengy.blockjail.JailSpigot.onlinePlayers;
import static org.bukkit.Bukkit.getServer;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        onlinePlayers.add(player);

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT nickname FROM jail WHERE uuid = ?"
            );
            statement.setString(1, player.getUniqueId().toString().replaceAll("-",""));
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String playerNick = result.getString("nickname");

                if(!player.getName().equals(playerNick)){
                    try {
                        PreparedStatement state = connection.prepareStatement(
                                "UPDATE jail SET nickname = ? WHERE uuid = ?"
                        );
                        state.setString(1, player.getName());
                        state.setString(2, player.getUniqueId().toString().replaceAll("-",""));
                        state.executeUpdate();


                    } catch (SQLException er) {
                        System.out.println("Failed read info from database: " + er.getMessage());
                    }
                }
            }

        } catch (SQLException er) {
            System.out.println("Failed read info from database: " + er.getMessage());
        }

        player.getInventory().clear();

        getServer().dispatchCommand(getServer().getConsoleSender(),"minecraft:give " + player.getName() + " netherite_pickaxe{display:{Name:'[{\"text\":\"Krumpac\",\"italic\":false}]'},CanDestroy:[obsidian]} 1");

        for (ItemStack item : player.getInventory().getContents()) {
            if(item != null && item.getType().equals(Material.NETHERITE_PICKAXE)){
                if (player.hasPermission("jail.5")) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.DIG_SPEED,5, true);
                    meta.setDisplayName(new MMessage().convertToString("<gradient:#55FFFF:#FFFFFF><bold>ᴋʀᴜᴍᴘáč</gradient> <gradient:#e41951:#ffffff><bold>ɪᴍᴘᴇʀɪᴜs"));
                    item.setItemMeta(meta);
                }
                else if (player.hasPermission("jail.4")) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.DIG_SPEED,4, true);
                    meta.setDisplayName(new MMessage().convertToString("<gradient:#55FFFF:#FFFFFF><bold>ᴋʀᴜᴍᴘáč</gradient> <gradient:#93d24c:#ffffff><bold>ʜᴇʀᴍᴇs"));
                    item.setItemMeta(meta);
                }
                else if (player.hasPermission("jail.3")) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.DIG_SPEED,3, true);
                    meta.setDisplayName(new MMessage().convertToString("<gradient:#55FFFF:#FFFFFF><bold>ᴋʀᴜᴍᴘáč</gradient> <gradient:#cf4242:#ffffff><bold>ɴᴇᴍᴇsɪs"));
                    item.setItemMeta(meta);
                }
                else if (player.hasPermission("jail.2")) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.DIG_SPEED,2, true);
                    meta.setDisplayName(new MMessage().convertToString("<gradient:#55FFFF:#FFFFFF><bold>ᴋʀᴜᴍᴘáč</gradient> <gradient:#68b0e2:#ffffff><bold>ᴀᴇʀɪs"));
                    item.setItemMeta(meta);
                }
                else if(player.hasPermission("jail.1")){
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.DIG_SPEED,1, true);
                    meta.setDisplayName(new MMessage().convertToString("<gradient:#55FFFF:#FFFFFF><bold>ᴋʀᴜᴍᴘáč</gradient> <gradient:#e2ac10:#ffffff><bold>ᴏʀɪᴏɴ"));
                    item.setItemMeta(meta);
                }
                else {
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.setDisplayName(new MMessage().convertToString("<gradient:#55FFFF:#FFFFFF><bold>ᴋʀᴜᴍᴘáč</gradient> <gradient:#aaaaaa:#ffffff><bold>ʜʀᴀᴄ"));
                    item.setItemMeta(meta);
                }
            }
        }


    }

}
