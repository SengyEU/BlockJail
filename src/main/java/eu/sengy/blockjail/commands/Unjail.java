package eu.sengy.blockjail.commands;

import eu.sengy.blockjail.JailBungee;
import eu.sengy.blockjail.utils.Config;
import eu.sengy.blockjail.utils.MMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static eu.sengy.blockjail.JailBungee.connection;
import static eu.sengy.blockjail.JailBungee.jailedPlayers;

public class Unjail extends Command implements TabExecutor {

    public Unjail() {
        super("unjail");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> arg = new ArrayList<>();

        if (args.length == 1) {
            arg.addAll(getJailPlayers());
        }
        return arg;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("jail.use")) {
            if (args.length == 1) {

                String jailedPlayerNick = args[0];

                String uuid = null;

                try {
                    PreparedStatement statement = JailBungee.connection.prepareStatement(
                            "SELECT uuid FROM jail WHERE nickname = ?"
                    );
                    statement.setString(1, jailedPlayerNick);
                    ResultSet result = statement.executeQuery();

                    if (result.next()) {
                        uuid = result.getString("uuid");
                    }

                } catch (SQLException er) {
                    System.out.println("Failed read info from database: " + er.getMessage());
                }
                if(uuid!=null){
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(jailedPlayerNick);

                    if(player!=null){
                        player.connect(ProxyServer.getInstance().getServerInfo("lobby"));
                    }
                    int id = 0;
                    int neededCount = 0;
                    Timestamp timestamp = null;
                    String admin = null;
                    String reason = null;

                    try {
                        PreparedStatement statement = JailBungee.connection.prepareStatement(
                                "SELECT id,neededCount,timestamp,admin,reason FROM jail WHERE uuid = ?"
                        );
                        statement.setString(1, uuid);
                        ResultSet result = statement.executeQuery();

                        if (result.next()) {
                            id = result.getInt("id");
                            neededCount = result.getInt("neededCount");
                            timestamp = result.getTimestamp("timestamp");
                            admin = result.getString("admin");
                            reason = result.getString("reason");
                        }

                    } catch (SQLException er) {
                        System.out.println("Failed read info from database: " + er.getMessage());
                    }

                    try {
                        PreparedStatement insert = connection.prepareStatement(
                                "INSERT INTO `jailHistory`(`id`,`uuid`, `nickname`, `bannedNickname`, `neededCount`, `reason`, `timestamp`, `admin`) " +
                                        "VALUES (?,?,?,?,?,?,?,?)"
                        );
                        insert.setInt(1, id);
                        insert.setString(2, uuid);
                        insert.setString(3, jailedPlayerNick);
                        insert.setString(4, jailedPlayerNick);
                        insert.setInt(5, neededCount);
                        insert.setString(6, reason);
                        insert.setTimestamp(7, timestamp);
                        insert.setString(8, admin);
                        insert.executeUpdate();

                    } catch (SQLException er) {
                        System.out.println("Failed read info from database: " + er.getMessage());
                    }

                    try {
                        PreparedStatement statement = JailBungee.connection.prepareStatement(
                                "DELETE FROM jail WHERE uuid = ?"
                        );
                        statement.setString(1, uuid);
                        statement.executeUpdate();

                        jailedPlayers.remove(uuid);

                    } catch (SQLException er) {
                        System.out.println("Failed read info from database: " + er.getMessage());
                    }

                    commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.unjail_unjailed").replace("%player%", jailedPlayerNick)));
                }
                else {
                    commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.unjail_notjailed").replace("%player%", jailedPlayerNick)));
                }
            }
            else {
                commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.unjail_usage")));
            }
        }
        else {
            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.nopermission")));
        }
    }

    private List<String> getJailPlayers(){

        List<String> players = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT nickname FROM jail"
            );
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                players.add(result.getString("nickname"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }
}
