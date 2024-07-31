package eu.sengy.blockjail.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.sengy.blockjail.JailSpigot;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessage  implements PluginMessageListener {

    JailSpigot plugin = JailSpigot.getInstance();

    @Override
    public void onPluginMessageReceived(String channel,Player player,byte[] message) {
        if(!channel.equals("BungeeCord"))return;
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subchannel = input.readUTF();
    }

    public void kickPlayer(Player player, String kickReason) {
        // Get the player by name

        if (player != null) {
            // Send a kick message to the target player
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("KickPlayer");
            output.writeUTF(player.getName());
            output.writeUTF(kickReason);

            player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
        }
    }
}
