package eu.sengy.blockjail.commands;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import eu.sengy.blockjail.utils.Config;
import eu.sengy.blockjail.utils.MMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static eu.sengy.blockjail.JailBungee.connection;
import static eu.sengy.blockjail.JailBungee.jailedPlayers;
import static eu.sengy.blockjail.utils.CenterMessage.Chat.sendCenteredMessageV2;
import static eu.sengy.blockjail.utils.CenterMessage.Chat.u;

public class Jail extends Command implements TabExecutor {

    public Jail() {
        super("jail");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> arg = new ArrayList<>();

        if (args.length == 1) {
            List<UUID> vanished = BungeeVanishAPI.getInvisiblePlayers();
            List<UUID> all = new ArrayList<>();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                all.add(player.getUniqueId());
            }

            all.removeAll(vanished);

            for (UUID uuid: all){
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                if (player != null){
                    String nick = player.getName();
                    arg.add(nick);
                }
            }
        }
        else if (args.length == 2) {
            arg.add("Počet Blocků");
        }
        else {
            arg.add("Důvod");
        }
        return arg;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("jail.use")) {
            if (args.length >= 3) {

                String jailedPlayerNick = args[0];
                ProxiedPlayer jailedPlayer = ProxyServer.getInstance().getPlayer(jailedPlayerNick);
                int duvodCount = args.length - 2;
                String[] duvodArgs = new String[duvodCount];
                System.arraycopy(args, 2, duvodArgs, 0, duvodCount);

                ServerInfo jail = ProxyServer.getInstance().getServerInfo("jail");

                int blockCount = Integer.parseInt(args[1]);
                String duvod = String.join(" ", duvodArgs);

                if(jailedPlayer == null){
                    String libre_host = "136.243.38.219:3306";
                    String libre_database = "s19_LibreLogin";
                    String libre_username = "u19_e5TolTJ6ss";
                    String libre_password = "1sNdfsFu=5DH.XF^f1Y6asim";
                    String libre_url = "jdbc:mysql://" + libre_host + "/" + libre_database + "?useSSL=false&autoReconnect=true";

                    try{
                        Connection libre_connection = DriverManager.getConnection(libre_url, libre_username, libre_password);
                        PreparedStatement libre_statement = libre_connection.prepareStatement(
                                "SELECT uuid FROM librepremium_data WHERE last_nickname = ?"
                        );
                        libre_statement.setString(1, jailedPlayerNick);
                        ResultSet libre_resultSet = libre_statement.executeQuery();

                        if(libre_resultSet.next()){
                            String lp_host = "136.243.38.219:3306";
                            String lp_database = "s19_luckperms";
                            String lp_username = "u19_BASlWsUzd8";
                            String lp_password = "wa^rrqF9R0I8KF.q.HNNlUNu";
                            String lp_url = "jdbc:mysql://" + lp_host + "/" + lp_database + "?useSSL=false&autoReconnect=true";

                            String libreUUID = libre_resultSet.getString("uuid");
                            String uuid = libreUUID.replaceAll("-","");

                            try{
                                Connection lp_connection = DriverManager.getConnection(lp_url, lp_username, lp_password);
                                PreparedStatement lp_statement = lp_connection.prepareStatement(
                                        "SELECT id FROM luckperms_user_permissions WHERE permission = ? AND value = ? AND uuid = ?"
                                );
                                lp_statement.setString(1, "jail.exempt");
                                lp_statement.setBoolean(2, true);
                                lp_statement.setString(3, libreUUID);
                                ResultSet lp_resultSet = lp_statement.executeQuery();

                                if(!lp_resultSet.next()){


                                    if (jailedPlayers.contains(uuid)) {
                                        commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_alreadyjailed").replace("%player%",jailedPlayerNick)));
                                    }
                                    else {

                                        commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_jailed").replace("%player%",jailedPlayerNick)));

                                        insertIntoJailDB(commandSender, jailedPlayerNick, blockCount, duvod, uuid);

                                    }

                                }
                                else {
                                    commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_exempt").replace("%player%",jailedPlayerNick)));
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_notonline").replace("%player%",jailedPlayerNick)));
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    if(!jailedPlayer.hasPermission("jail.exempt")){

                        String uuid = jailedPlayer.getUUID();

                        if (jailedPlayers.contains(uuid)) {
                            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_alreadyjailed").replace("%player%",jailedPlayerNick)));
                        }
                        else {

                            for (String line : Config.getConfig().getStringList("commands.jail_jailedplayer")){

                                String replace = line
                                        .replace("%blockcount%", String.valueOf(blockCount))
                                        .replace("%reason%", duvod)
                                        .replace("%admin%", commandSender.getName());

                                if (replace.contains("<center>")) {
                                    sendCenteredMessageV2(jailedPlayer, BaseComponent.toLegacyText(MMessage.bungeeConvertToString(replace)).replaceAll("<center>",""));
                                }
                                else {
                                    jailedPlayer.sendMessage(MMessage.bungeeConvertToString(replace));
                                }

                            }

                            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_jailed").replace("%player%",jailedPlayerNick)));
                            jailedPlayer.connect(jail);

                            insertIntoJailDB(commandSender, jailedPlayerNick, blockCount, duvod, uuid);

                        }
                    }
                    else {
                        commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_exempt").replace("%player%",jailedPlayerNick)));
                    }
                }
            }
            else {
                commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jail_usage")));
            }
        }
        else {
            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.nopermission")));
        }
    }

    private void insertIntoJailDB(CommandSender commandSender, String jailedPlayerNick, int blockCount, String duvod, String uuid) {
        try {
            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO `jail`(`uuid`, `nickname`, `neededCount`, `currCount`, `reason`, `timestamp`, `admin`) " +
                            "VALUES (?,?,?,?,?,?,?)"
            );
            insert.setString(1, uuid);
            insert.setString(2, jailedPlayerNick);
            insert.setInt(3, blockCount);
            insert.setInt(4, 0);
            insert.setString(5, duvod);
            insert.setTimestamp(6, new Timestamp(new Date().getTime()));
            insert.setString(7, commandSender.getName());
            insert.executeUpdate();

            if(!jailedPlayers.contains(uuid)){
                jailedPlayers.add(uuid);
            }

        } catch (SQLException er) {
            System.out.println("Failed read info from database: " + er.getMessage());
        }
    }
}
