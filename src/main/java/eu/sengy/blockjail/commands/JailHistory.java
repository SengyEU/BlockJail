package eu.sengy.blockjail.commands;

import eu.sengy.blockjail.JailBungee;
import eu.sengy.blockjail.utils.Config;
import eu.sengy.blockjail.utils.MMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static eu.sengy.blockjail.JailBungee.connection;
import static eu.sengy.blockjail.utils.CenterMessage.Chat.*;

public class JailHistory extends Command implements TabExecutor {

    public JailHistory() {
        super("jailhistory");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> arg = new ArrayList<>();

        if (args.length == 1) {
            arg.addAll(getJailHistory());
            arg.add("*global*");
        }
        return arg;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("jail.use")) {
            if (args.length >= 1) {

                int page = 1;

                if(args.length == 2){
                    page = Integer.parseInt(args[1]);
                }

                int pages = 1;


                String nick = args[0];

                try {

                    PreparedStatement count;

                    if(nick.equalsIgnoreCase("*global*")){
                        count = JailBungee.connection.prepareStatement(
                                "SELECT COUNT(*) as counttt FROM jailHistory"
                        );
                    }
                    else {
                        count = JailBungee.connection.prepareStatement(
                                "SELECT COUNT(*) as counttt FROM jailHistory WHERE nickname = ?"
                        );
                        count.setString(1, nick);
                    }
                    ResultSet rc = count.executeQuery();
                    if(rc.next()){
                        pages = rc.getInt("counttt");
                    }
                    if(page > pages/2){
                        page = pages/2;
                    }
                    if(page < 1){
                        page = 1;
                    }
                    if(pages <= 1){
                        pages=2;
                    }

                    PreparedStatement statement;

                    if(nick.equalsIgnoreCase("*global*")){
                        statement = JailBungee.connection.prepareStatement(
                                "SELECT * FROM jailHistory LIMIT 2 OFFSET ?"
                        );
                        statement.setInt(1, page * 2 - 2);
                    }
                    else{
                        statement = JailBungee.connection.prepareStatement(
                                "SELECT * FROM jailHistory WHERE nickname = ? LIMIT 2 OFFSET ?"
                        );
                        statement.setString(1, nick);
                        statement.setInt(2, page * 2 - 2);
                    }

                    ResultSet result = statement.executeQuery();
                    

                    if(nick.equalsIgnoreCase("*global*")){
                        String global_header = Config.getConfig().getString("jailhistory.global_header").replace("%player%", nick);

                        if (global_header.contains("<center>")) {
                            sendCenteredMessageV2(commandSender, BaseComponent.toLegacyText(MMessage.bungeeConvertToString(global_header)).replaceAll("<center>",""));
                        }
                        else {
                            commandSender.sendMessage(MMessage.bungeeConvertToString(global_header));
                        }
                    }
                    else{
                        String header = Config.getConfig().getString("jailhistory.header").replace("%player%", nick);
                        
                        if (header.contains("<center>")) {
                            sendCenteredMessageV2(commandSender, BaseComponent.toLegacyText(MMessage.bungeeConvertToString(header)).replaceAll("<center>",""));
                        }
                        else {
                            commandSender.sendMessage(MMessage.bungeeConvertToString(header));
                        }
                    }

                    while (result.next()) {
                        String player = null;
                        if(nick.equalsIgnoreCase("*global*")){
                            player = result.getString("nickname");
                        }
                        int id = result.getInt("id");
                        String jailnick = result.getString("bannedNickname");
                        int blockcount = result.getInt("neededCount");
                        String reason = result.getString("reason");
                        String admin = result.getString("admin");
                        String timestamp = new SimpleDateFormat("dd. MM. yyyy").format(result.getTimestamp("timestamp"));

                        for (String line : Config.getConfig().getStringList("jailhistory.body")){

                            String replace = line
                                    .replace("%player%", nick)
                                    .replace("%id%", String.valueOf(id))
                                    .replace("%jailnick%", jailnick)
                                    .replace("%blockcount%", String.valueOf(blockcount))
                                    .replace("%reason%", reason)
                                    .replace("%admin%", admin)
                                    .replace("%timestamp%", timestamp);

                            if(nick.equalsIgnoreCase("*global*")){
                                commandSender.sendMessage(MMessage.bungeeConvertToString(replace.replaceAll("%global%", "<newline>" + Config.getConfig().getString("jailhistory.body_global").replace("%player%", player))));
                            }
                            else{
                                commandSender.sendMessage(MMessage.bungeeConvertToString(replace.replaceAll("%global%","")));
                            }
                        }
                    }

                    TextComponent footer_left = new TextComponent(MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.footer_left").replaceAll("<center>","")));
                    TextComponent arrow_left = new TextComponent(MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.arrow_left").replaceAll("%page_left%", String.valueOf(page))));
                    arrow_left.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/jailhistory " + nick + " " + (page-1)));
                    arrow_left.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.arrow_left_hover"))));
                    TextComponent footer_center = new TextComponent(MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.footer_center")));
                    TextComponent arrow_right = new TextComponent(MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.arrow_right").replaceAll("%page_right%", String.valueOf(pages / 2))));
                    arrow_right.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/jailhistory " + nick + " " + (page+1)));
                    arrow_right.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.arrow_right_hover"))));
                    TextComponent footer_right = new TextComponent(MMessage.bungeeConvertToString(Config.getConfig().getString("jailhistory.footer_right")));

                    BaseComponent footer = new TextComponent();
                    footer.addExtra(footer_left);
                    footer.addExtra(arrow_left);
                    footer.addExtra(footer_center);
                    footer.addExtra(arrow_right);
                    footer.addExtra(footer_right);

                    BaseComponent finalFooter = new TextComponent();
                    finalFooter.addExtra(mezery(footer.toLegacyText()));
                    finalFooter.addExtra(footer);

                    if(Config.getConfig().getString("jailhistory.footer_left").contains("<center>")){
                        commandSender.sendMessage(finalFooter);
                    }
                    else {
                        commandSender.sendMessage(footer);
                    }




                } catch (SQLException er) {
                    System.out.println("Failed read info from database: " + er.getMessage());
                }
            }
            else {
                commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.jailhistory_usage")));
            }
        }
        else {
            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.nopermission")));
        }
    }

    private List<String> getJailHistory(){

        List<String> players = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT nickname FROM jailHistory"
            );
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                if(!players.contains(result.getString("nickname"))){
                    players.add(result.getString("nickname"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }
}
