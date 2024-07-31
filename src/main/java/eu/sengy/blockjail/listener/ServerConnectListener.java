package eu.sengy.blockjail.listener;

import eu.sengy.blockjail.JailBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static eu.sengy.blockjail.JailBungee.connection;
import static eu.sengy.blockjail.JailBungee.jailedPlayers;

public class ServerConnectListener implements Listener {

    @EventHandler
    public void ServerConnectEvent(ServerConnectEvent e) {

        ServerInfo jail = ProxyServer.getInstance().getServerInfo("jail");

        if (e.getTarget().getName().equals("lobby")) {
            String playerUUID = e.getPlayer().getUniqueId().toString().replaceAll("-", "");


            if (jailedPlayers.contains(playerUUID)) {

                    jail.ping((resul, error) -> {
                        if(error == null || resul != null) {
                            e.getPlayer().connect(jail);
                        }
                        else {
                            e.getPlayer().disconnect("Jail je nedostupný.");
                        }
                    });
            }

            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT nickname FROM jailHistory WHERE uuid = ?"
                );
                statement.setString(1, playerUUID);
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    String nickname = result.getString("nickname");
                    if(!nickname.equals(e.getPlayer().getName())){
                        try {
                            PreparedStatement state = JailBungee.connection.prepareStatement(
                                    "UPDATE jailHistory SET nickname = ? WHERE uuid = ?"
                            );
                            state.setString(1, e.getPlayer().getName());
                            state.setString(2, e.getPlayer().getUniqueId().toString().replaceAll("-",""));
                            state.executeUpdate();


                        } catch (SQLException er) {
                            System.out.println("Failed read info from database: " + er.getMessage());
                        }
                    }
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
                e.getPlayer().disconnect("Nastala chyba, obrať se na naší discord podporu.");
            }
        }
    }
}
