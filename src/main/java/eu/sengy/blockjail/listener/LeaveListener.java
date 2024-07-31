package eu.sengy.blockjail.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static eu.sengy.blockjail.JailSpigot.onlinePlayers;

public class LeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        onlinePlayers.remove(e.getPlayer());
    }

}
